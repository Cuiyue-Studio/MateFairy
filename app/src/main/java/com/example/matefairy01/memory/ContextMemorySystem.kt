package com.example.matefairy01.memory

import android.util.Log
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.memory.permanent.PermanentStore
import com.example.matefairy01.memory.retrieval.MemoryRetriever
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

/**
 * 智能上下文管理系统
 * 采用 滑动窗口 (Short-term) + 异步并行压缩缓存 (Long-term) 策略。
 *
 * P1.5 起，新增可选注入：
 * - [permanentStore] 提供 L4 永久人设（SOUL.md / USER.md），注入到 system prompt
 * - [memoryRetriever] 按当前 query 召回 L2 episodic + L3 facts，注入为"# 相关回忆"段
 *
 * 注入是惰性可选的：构造时不传也能跑（保持旧行为不变）。
 */
class ContextMemorySystem(
    private val llmProvider: ILLMProvider,
    private val windowSize: Int = 3,
    private val permanentStore: PermanentStore? = null,
    private val memoryRetriever: MemoryRetriever? = null
) {
    companion object {
        private const val TAG = "ContextMemorySystem"
    }

    private val compressionLock = Any()

    // 长期记忆缓存（压缩后的摘要）
    private var longTermMemorySummary: String = ""

    // 短期记忆窗口（最近N轮对话）
    private val shortTermMemory = LinkedList<ChatMessage>()
    private val pendingCompressionMessages = mutableListOf<ChatMessage>()

    private val scope = CoroutineScope(Dispatchers.IO)
    @Volatile
    private var compressionJobRunning = false

    /**
     * 组装最终发给 LLM 的对话 Prompt。
     *
     * 优先级（从前到后注入到 system 段）：
     * 1. 基础 system prompt（[ILLMProvider.systemPrompt]）+ SOUL.md
     * 2. USER.md（用户画像）
     * 3. 当前 query 召回的 # 相关回忆（来自 L2 + L3）
     * 4. 长期摘要（旧版逻辑保留）
     * 5. 短期对话窗口
     *
     * @param query 当前用户输入；非空时触发 [memoryRetriever] 召回
     */
    suspend fun buildPromptMessages(query: String? = null): List<ChatMessage> {
        val messages = mutableListOf<ChatMessage>()

        // 1. 基础 system prompt + SOUL.md
        val soulText = permanentStore?.readSoul()?.trim().orEmpty()
        val systemPrompt = if (soulText.isNotEmpty()) {
            "${llmProvider.systemPrompt}\n\n$soulText"
        } else {
            llmProvider.systemPrompt
        }
        messages.add(ChatMessage(role = "system", content = systemPrompt))

        // 2. USER.md（用户画像）
        val userText = permanentStore?.readUser()?.trim().orEmpty()
        if (userText.isNotEmpty()) {
            messages.add(ChatMessage(role = "system", content = userText))
        }

        // 3. 当前 query 召回的相关回忆
        if (!query.isNullOrBlank() && memoryRetriever != null) {
            val recall = runCatching { memoryRetriever.assembleContext(query) }
                .onFailure { Log.w(TAG, "memory recall failed: ${it.message}") }
                .getOrDefault("")
            if (recall.isNotBlank()) {
                messages.add(ChatMessage(role = "system", content = recall))
            }
        }

        // 4. 历史摘要缓存
        if (longTermMemorySummary.isNotEmpty()) {
            messages.add(
                ChatMessage(
                    role = "system",
                    content = "Previous memory summary: $longTermMemorySummary"
                )
            )
        }

        // 5. 最近 N 轮对话
        messages.addAll(shortTermMemory)

        return messages
    }

    /**
     * 添加新的对话记录（User/Assistant），并触发滑动与压缩
     */
    fun addMessage(message: ChatMessage) {
        shortTermMemory.add(message)

        // 检查是否超过滑动窗口限制 (一轮对话包含 User + Assistant，因此 size 可能是 windowSize * 2)
        val maxMessages = windowSize * 2
        if (shortTermMemory.size > maxMessages) {
            val poppedMessages = mutableListOf<ChatMessage>()
            // 弹出最早的一轮对话 (User + Assistant)
            poppedMessages.add(shortTermMemory.removeFirst())
            poppedMessages.add(shortTermMemory.removeFirst())

            // 在后台协程中异步压缩
            triggerAsyncCompression(poppedMessages)
        }
    }

    /** 取最近 N 条短期消息的快照（供 IngestionWorker.ExtractFactsJob 使用） */
    fun snapshotRecentMessages(maxCount: Int = 6): List<ChatMessage> {
        synchronized(shortTermMemory) {
            if (shortTermMemory.isEmpty()) return emptyList()
            val from = (shortTermMemory.size - maxCount).coerceAtLeast(0)
            return shortTermMemory.subList(from, shortTermMemory.size).toList()
        }
    }

    /**
     * 后台异步并行压缩
     */
    private fun triggerAsyncCompression(messagesToCompress: List<ChatMessage>) {
        synchronized(compressionLock) {
            pendingCompressionMessages += messagesToCompress
            if (compressionJobRunning) {
                return
            }
            compressionJobRunning = true
        }

        scope.launch {
            try {
                while (true) {
                    val batch =
                        synchronized(compressionLock) {
                            if (pendingCompressionMessages.isEmpty()) {
                                compressionJobRunning = false
                                return@launch
                            }

                            pendingCompressionMessages.toList().also {
                                pendingCompressionMessages.clear()
                            }
                        }

                    // 将累计滑出的消息合并摘要，避免同一时间并发触发多次网络压缩
                    val newSummary = llmProvider.summarize(batch)

                    longTermMemorySummary = if (longTermMemorySummary.isEmpty()) {
                        newSummary
                    } else {
                        "$longTermMemorySummary | $newSummary"
                    }

                    Log.d("ContextMemorySystem", "Async compression completed. Summary updated.")
                }
            } catch (e: Exception) {
                synchronized(compressionLock) {
                    compressionJobRunning = false
                }
                Log.e("ContextMemorySystem", "Failed to compress memory", e)
                // 如果压缩失败，可以选择暂时重新放回或忽略，取决于容错策略
            }
        }
    }
    
    /**
     * 清空记忆（用于重新设定）
     */
    fun clearMemory() {
        shortTermMemory.clear()
        longTermMemorySummary = ""
    }
}
