package com.example.matefairy01.memory.ingestion

import android.util.Log
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.memory.episodic.EpisodicStore

/**
 * Consolidator：把一段已经"老去"的对话片段压缩为摘要，写入 L2 EpisodicStore。
 *
 * 触发链路（详见 P1.5）：
 *   ConversationOrchestrator.afterReply
 *      └─ tokens > 阈值 OR turn % 10 == 0
 *           └─ IngestionWorker.enqueue(ConsolidateJob(messages, turnRange))
 *                └─ Consolidator.run(messages) → llmProvider.summarize → episodicStore.add
 *
 * 失败安全：summarize 抛异常或返回空字符串则跳过入库；不影响主对话。
 */
class Consolidator(
    private val llmProvider: ILLMProvider,
    private val episodicStore: EpisodicStore
) {
    companion object {
        private const val TAG = "Consolidator"
    }

    /**
     * @param messages 待压缩的对话片段（user + assistant 轮次）
     * @param turnRange "12-21" 等可读区间，用于溯源
     * @return 写入的 episodic id；失败返回 null
     */
    suspend fun run(messages: List<ChatMessage>, turnRange: String? = null): Long? {
        if (messages.isEmpty()) return null

        val summary = runCatching { llmProvider.summarize(messages) }
            .onFailure { Log.w(TAG, "summarize failed: ${it.message}") }
            .getOrNull()
            ?.trim()
            .orEmpty()

        if (summary.isBlank()) {
            Log.d(TAG, "summary empty, skip")
            return null
        }

        // importance 启发式：消息越多、越长，越可能含有持久信息
        val importance = estimateImportance(messages, summary)

        return runCatching {
            episodicStore.add(
                content = summary,
                importance = importance,
                sourceTurnRange = turnRange
            )
        }.onFailure { Log.w(TAG, "episodicStore.add failed: ${it.message}") }
            .getOrNull()
    }

    private fun estimateImportance(messages: List<ChatMessage>, summary: String): Int {
        val totalChars = messages.sumOf { it.content.length }
        // 粗略映射：< 100 → 3，100-300 → 5，300-800 → 7，> 800 → 8
        // 摘要中含明显事实词加 1（"喜欢/讨厌/计划/记得/生日/家人/朋友"等）
        val base = when {
            totalChars < 100 -> 3
            totalChars < 300 -> 5
            totalChars < 800 -> 7
            else -> 8
        }
        val factHinted = listOf("喜欢", "讨厌", "计划", "记得", "生日", "家人", "朋友", "工作", "目标")
            .any { summary.contains(it) }
        return (if (factHinted) base + 1 else base).coerceIn(1, 10)
    }
}
