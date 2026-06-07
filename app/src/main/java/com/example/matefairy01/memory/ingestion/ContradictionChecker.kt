package com.example.matefairy01.memory.ingestion

import android.util.Log
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.memory.semantic.Fact
import com.example.matefairy01.memory.semantic.SemanticStore
import org.json.JSONArray
import org.json.JSONObject

/**
 * 检查新事实是否与已有事实矛盾，若矛盾用 [SemanticStore.supersede] 标记旧事实被替代。
 *
 * 流程（抄 NAVI）：
 * 1. 候选过滤：从同 category 的活跃事实中按 entity token 粗筛
 *    - 只比较"看起来可能相关"的旧事实，避免 LLM 调用爆炸
 * 2. LLM 判定：把新事实 + 候选旧事实丢给 LLM，让它返回应当被 supersede 的旧 id 列表
 * 3. 写入 supersede 标记（不删除）
 *
 * 失败安全：任何环节抛异常仅打 warning，不影响新事实写入。
 */
class ContradictionChecker(
    private val llmProvider: ILLMProvider,
    private val semanticStore: SemanticStore
) {
    companion object {
        private const val TAG = "ContradictionChecker"

        /** 候选过滤阈值：同 category 活跃事实最多取多少条做 LLM 判定 */
        private const val MAX_CANDIDATES = 6

        private val SYSTEM_PROMPT = """
            你是事实矛盾检测器。给定一条"新事实"和若干条"已有事实"，
            判定哪些已有事实与新事实直接矛盾（同一主语 + 同一谓项的相反陈述）。
            
            【输出格式】
            必须只输出一个 JSON：{"supersededIds":[id1, id2, ...]}
            
            【判定规则】
            1. 只标记直接矛盾。同主题但不矛盾的不标记（例如 "喜欢咖啡" 与 "喜欢茶" 不矛盾）。
            2. 时间维度的更新算矛盾（例如 "用户在上海工作" 与 "用户在北京工作"）。
            3. 不能确定时不标记。
            4. 输出仅 JSON，不要解释、不要代码块。
        """.trimIndent()
    }

    /**
     * 检查并把矛盾的旧事实 supersede。返回被标记的旧 id 列表。
     */
    suspend fun checkAndSupersede(
        newId: Long,
        newCategory: String,
        newContent: String
    ): List<Long> {
        val candidates = runCatching {
            semanticStore.listActiveByCategory(newCategory, MAX_CANDIDATES)
        }.onFailure { Log.w(TAG, "list candidates failed: ${it.message}") }
            .getOrNull()
            ?.filter { it.id != newId }
            ?: return emptyList()

        if (candidates.isEmpty()) return emptyList()

        // entity token 粗筛：用简单字面共现做候选过滤，省 LLM 调用
        val sharedTokens = extractEntityTokens(newContent)
        val narrowed = candidates.filter { c -> hasOverlap(extractEntityTokens(c.content), sharedTokens) }
        val finalCandidates = narrowed.ifEmpty { candidates }.take(MAX_CANDIDATES)

        val raw = runCatching {
            llmProvider.complete(
                systemPrompt = SYSTEM_PROMPT,
                userMessage = buildPrompt(newContent, finalCandidates),
                maxTokens = 120,
                temperature = 0.0
            )
        }.onFailure { Log.w(TAG, "llm complete failed: ${it.message}") }
            .getOrNull()
            .orEmpty()

        if (raw.isBlank()) return emptyList()

        val ids = parseSupersededIds(raw)
        if (ids.isEmpty()) return emptyList()

        val now = System.currentTimeMillis()
        val applied = mutableListOf<Long>()
        for (oldId in ids) {
            if (oldId == newId) continue
            val ok = runCatching { semanticStore.supersede(oldId, newId, now) }
                .getOrDefault(false)
            if (ok) applied += oldId
        }
        if (applied.isNotEmpty()) {
            Log.d(TAG, "supersede applied: ${applied.joinToString()} → #$newId")
        }
        return applied
    }

    private fun buildPrompt(newContent: String, candidates: List<Fact>): String {
        val sb = StringBuilder()
        sb.appendLine("【新事实】")
        sb.appendLine(newContent.trim())
        sb.appendLine()
        sb.appendLine("【已有事实】")
        for (c in candidates) {
            sb.append('#').append(c.id).append("：").appendLine(c.content.trim())
        }
        return sb.toString()
    }

    private fun parseSupersededIds(raw: String): List<Long> {
        val cleaned = raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val obj = runCatching { JSONObject(cleaned) }
            .recoverCatching {
                val s = cleaned.indexOf('{')
                val e = cleaned.lastIndexOf('}')
                if (s >= 0 && e > s) JSONObject(cleaned.substring(s, e + 1)) else throw it
            }
            .getOrNull() ?: return emptyList()

        val arr = obj.optJSONArray("supersededIds") ?: JSONArray()
        val out = ArrayList<Long>(arr.length())
        for (i in 0 until arr.length()) {
            val v = arr.optLong(i, -1L)
            if (v > 0L) out += v
        }
        return out
    }

    /**
     * 极简 entity token：取连续的 2-3 字中文片段。规则简单，召回率优先；
     * 真正的判定交给 LLM。
     */
    private fun extractEntityTokens(text: String): Set<String> {
        if (text.isBlank()) return emptySet()
        val out = HashSet<String>()
        val cleaned = text.replace(Regex("[\\s，。！？、,.!?]"), "")
        for (i in 0..cleaned.length - 2) {
            out += cleaned.substring(i, i + 2)
            if (i + 3 <= cleaned.length) out += cleaned.substring(i, i + 3)
        }
        return out
    }

    private fun hasOverlap(a: Set<String>, b: Set<String>): Boolean {
        if (a.isEmpty() || b.isEmpty()) return false
        val (small, large) = if (a.size <= b.size) a to b else b to a
        for (s in small) if (s in large) return true
        return false
    }
}
