package com.example.matefairy01.memory.permanent

import android.util.Log
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.memory.DreamConfig
import com.example.matefairy01.memory.episodic.EpisodicDao
import com.example.matefairy01.memory.semantic.Fact
import com.example.matefairy01.memory.semantic.SemanticStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dream：周期性把 L2 episodic 摘要 + L3 facts 蒸馏到 L4 md 文件。
 *
 * 触发（详见 P1.5）：
 * - 前台空闲 5 分钟
 * - 手柄 Debug 按键强制触发
 * - **不在 onPause 跑**：Cached App 易被杀，会留半成品 md
 *
 * P1 实现策略（最小可用）：
 * - USER.md：基于全部活跃 facts 全量重写（小规模可接受）
 * - MEMORY.md：基于近 [DreamConfig.maxBatchSize] 条 episodic 全量重写
 * - SOUL.md：永远不动
 * - 多次并发触发用 [mutex] 保证串行
 *
 * P2 升级：增量改写（仿 nanobot Dream "外科手术式 edit"）+ JGit 版本化。
 */
class DreamJob(
    private val llmProvider: ILLMProvider,
    private val episodicDao: EpisodicDao,
    private val semanticStore: SemanticStore,
    private val permanentStore: PermanentStore,
    private val dreamConfig: DreamConfig
) {
    companion object {
        private const val TAG = "DreamJob"
        private val DATE_FMT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        private val USER_DISTILL_PROMPT = """
            你是用户画像生成器。基于"已知事实列表"，生成一份用户画像 markdown 文件。
            
            【输出格式】
            标题为 "# USER · 用户画像"，下设若干二级章节（## 偏好 / ## 习惯 / ## 身份信息 / ## 厌恶 等）。
            每章节用无序列表罗列事实，每条不超过 30 字。
            事实少时章节可省略。
            
            【强制约束】
            1. 只输出 markdown，不要解释、不要前后缀、不要代码块
            2. 严格基于给定事实，不要编造
            3. 同主题相关事实合并表述，避免重复
            4. 输出长度控制在 800 字以内
        """.trimIndent()

        private val MEMORY_DISTILL_PROMPT = """
            你是叙事记忆生成器。基于"近期会话摘要列表"，生成一份跨会话叙事 markdown。
            
            【输出格式】
            标题为 "# MEMORY · 跨会话叙事记忆"，按时间倒序记录值得保留的事件、决定、共同经历。
            每条用无序列表，格式：`- [日期] 事件描述（不超过 40 字）`
            
            【强制约束】
            1. 只输出 markdown，不要解释、不要前后缀、不要代码块
            2. 仅保留值得跨会话回忆的内容（共同经历 / 决定 / 重要话题）
            3. 不抄录琐碎闲聊
            4. 同主题多条合并叙述
            5. 输出长度控制在 1000 字以内
        """.trimIndent()
    }

    private val mutex = Mutex()

    /**
     * 跑一次完整 Dream。串行保护：并发调用会等前一次跑完。
     *
     * @param force 即使无新增数据也强制重写；默认 false
     */
    suspend fun run(force: Boolean = false): Boolean = mutex.withLock {
        try {
            permanentStore.ensureInitialized()
            val ranUser = distillUser(force)
            val ranMemory = distillMemory(force)
            Log.i(TAG, "Dream done. user=$ranUser memory=$ranMemory")
            ranUser || ranMemory
        } catch (e: Throwable) {
            Log.w(TAG, "Dream failed: ${e.message}", e)
            false
        }
    }

    // ------------------------------------------------------------------

    private suspend fun distillUser(force: Boolean): Boolean {
        val facts = semanticStore.listActiveFacts(limit = 100)
        if (facts.isEmpty() && !force) return false

        val current = permanentStore.readUser()
        val factText = formatFactsForPrompt(facts)
        val userMessage = buildString {
            appendLine("【已知事实列表】")
            appendLine(factText.ifBlank { "（暂无）" })
            appendLine()
            appendLine("【当前 USER.md（仅供参考，可全量重写）】")
            appendLine(current.take(2000))
        }

        val newContent = runCatching {
            llmProvider.complete(
                systemPrompt = USER_DISTILL_PROMPT,
                userMessage = userMessage,
                maxTokens = 800,
                temperature = 0.3
            )
        }.onFailure { Log.w(TAG, "distillUser llm failed: ${it.message}") }
            .getOrNull()
            ?.let { sanitize(it) }
            .orEmpty()

        if (newContent.isBlank()) return false
        if (!force && newContent.trim() == current.trim()) return false

        permanentStore.writeUser(newContent)
        return true
    }

    private suspend fun distillMemory(force: Boolean): Boolean {
        val now = System.currentTimeMillis()
        val lookbackMs = 30L * 24L * 3600L * 1000L  // 近 30 天
        val episodes = episodicDao.recentSince(
            sinceTimestamp = now - lookbackMs,
            limit = dreamConfig.maxBatchSize.coerceAtLeast(1)
        )
        if (episodes.isEmpty() && !force) return false

        val current = permanentStore.readMemory()
        val episodeText = episodes.joinToString("\n") { e ->
            "- [${DATE_FMT.format(Date(e.timestamp))}] (importance=${e.importance}) ${e.content.trim()}"
        }
        val userMessage = buildString {
            appendLine("【近期会话摘要列表（按时间倒序）】")
            appendLine(episodeText.ifBlank { "（暂无）" })
            appendLine()
            appendLine("【当前 MEMORY.md（仅供参考，可全量重写）】")
            appendLine(current.take(2000))
        }

        val newContent = runCatching {
            llmProvider.complete(
                systemPrompt = MEMORY_DISTILL_PROMPT,
                userMessage = userMessage,
                maxTokens = 1000,
                temperature = 0.3
            )
        }.onFailure { Log.w(TAG, "distillMemory llm failed: ${it.message}") }
            .getOrNull()
            ?.let { sanitize(it) }
            .orEmpty()

        if (newContent.isBlank()) return false
        if (!force && newContent.trim() == current.trim()) return false

        permanentStore.writeMemory(newContent)
        return true
    }

    private fun formatFactsForPrompt(facts: List<Fact>): String {
        return facts.joinToString("\n") { f ->
            "- [${f.category}] ${f.content.trim()}（置信 ${"%.2f".format(f.confidence)}）"
        }
    }

    private fun sanitize(raw: String): String {
        return raw.trim()
            .removePrefix("```markdown")
            .removePrefix("```md")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
    }
}
