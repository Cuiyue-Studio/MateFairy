package com.example.matefairy01.memory.ingestion

import android.util.Log
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.memory.semantic.SemanticStore
import org.json.JSONArray
import org.json.JSONObject

/**
 * 从最近一段对话里抽取持久事实（用户偏好/技能/习惯/身份等），写入 L3 SemanticStore。
 *
 * 输出契约：LLM 返回 JSON `{"facts": [{"category": "...", "content": "...", "confidence": 0.x}, ...]}`
 *   - category 自由小写英文 tag：preference / dislike / skill / habit / identity / relation / event
 *   - content 一句话陈述句，主语建议为"用户"
 *   - confidence 0..1
 *
 * 失败容错：
 *   - JSON 解析失败 → 尝试 [extractJsonObject] 截取首个 {…}
 *   - 仍失败 → 返回空 list，不阻塞写入流程
 *   - 单条 fact 字段缺失 → 跳过该条
 *
 * 矛盾处理由 [ContradictionChecker] 负责，本类仅做"抽取"。
 */
class FactExtractor(
    private val llmProvider: ILLMProvider,
    private val semanticStore: SemanticStore,
    private val contradictionChecker: ContradictionChecker
) {
    companion object {
        private const val TAG = "FactExtractor"
        private const val MAX_FACTS_PER_CALL = 8

        private val SYSTEM_PROMPT = """
            你是事实抽取器。从给定的对话片段中提取关于"用户"的持久事实。
            
            【输出格式】
            必须只输出一个 JSON 对象：
            {"facts":[{"category":"...","content":"...","confidence":0.0~1.0}, ...]}
            
            【字段规则】
            1. category 取以下小写英文 tag 之一：preference / dislike / skill / habit / identity / relation / event
            2. content 一句中文陈述句，必须以"用户"开头，简洁不超过 40 字
            3. confidence 0.0~1.0，越确定越高（陈述明确给 0.85+，推断给 0.6~0.7）
            
            【强制约束】
            1. 只输出一个 JSON，不要 markdown 代码块、不要解释、不要前后缀
            2. 没有可抽取的事实时返回：{"facts":[]}
            3. 不要重复抽取已存在的事实
            4. 不要把临时情绪当作事实（"用户今天有点累"不算）
            5. 单次最多抽取 ${MAX_FACTS_PER_CALL} 条；优先选高置信
        """.trimIndent()
    }

    /**
     * 抽取并写入。返回写入的 fact id 列表。
     * 调用方负责事先做 [ShouldIngest] 启发式过滤。
     */
    suspend fun extract(messages: List<ChatMessage>): List<Long> {
        if (messages.isEmpty()) return emptyList()

        val conversation = formatConversation(messages)
        val raw = runCatching {
            llmProvider.complete(
                systemPrompt = SYSTEM_PROMPT,
                userMessage = conversation,
                maxTokens = 400,
                temperature = 0.2
            )
        }.onFailure { Log.w(TAG, "complete failed: ${it.message}") }
            .getOrNull()
            .orEmpty()

        if (raw.isBlank()) return emptyList()

        val facts = parseFactsJson(raw)
        if (facts.isEmpty()) {
            Log.d(TAG, "no facts extracted")
            return emptyList()
        }

        val written = mutableListOf<Long>()
        for (f in facts.take(MAX_FACTS_PER_CALL)) {
            val newId = runCatching {
                semanticStore.addFact(
                    category = f.category,
                    content = f.content,
                    confidence = f.confidence
                )
            }.onFailure { Log.w(TAG, "addFact failed: ${it.message}") }
                .getOrNull() ?: continue

            // 矛盾检测：异步交给 ContradictionChecker，发现冲突就 supersede 旧条
            runCatching { contradictionChecker.checkAndSupersede(newId, f.category, f.content) }
                .onFailure { Log.w(TAG, "contradiction check failed: ${it.message}") }

            written += newId
        }
        Log.d(TAG, "extracted ${written.size} facts")
        return written
    }

    // ------------------------------------------------------------------

    private data class ExtractedFact(
        val category: String,
        val content: String,
        val confidence: Float
    )

    private fun formatConversation(messages: List<ChatMessage>): String {
        val sb = StringBuilder("【对话片段】\n")
        for (m in messages) {
            val role = when (m.role) {
                "user" -> "用户"
                "assistant" -> "精灵"
                else -> m.role
            }
            sb.append(role).append("：").append(m.content.trim()).append('\n')
        }
        return sb.toString()
    }

    private fun parseFactsJson(raw: String): List<ExtractedFact> {
        val cleaned = stripCodeFence(raw).trim()
        // 优先直接解析；失败再用首尾 {…} 提取
        val obj = runCatching { JSONObject(cleaned) }
            .recoverCatching {
                val start = cleaned.indexOf('{')
                val end = cleaned.lastIndexOf('}')
                if (start >= 0 && end > start) JSONObject(cleaned.substring(start, end + 1))
                else throw it
            }
            .getOrNull() ?: return emptyList()

        val arr = obj.optJSONArray("facts") ?: JSONArray()
        val out = ArrayList<ExtractedFact>(arr.length())
        for (i in 0 until arr.length()) {
            val item = arr.optJSONObject(i) ?: continue
            val category = item.optString("category").trim().lowercase()
            val content = item.optString("content").trim()
            val confidence = item.optDouble("confidence", 0.7).toFloat().coerceIn(0f, 1f)
            if (category.isBlank() || content.isBlank()) continue
            if (content.length > 80) continue   // 防止 LLM 跑题输出长文
            out += ExtractedFact(category, content, confidence)
        }
        return out
    }

    private fun stripCodeFence(raw: String): String {
        return raw.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }
}
