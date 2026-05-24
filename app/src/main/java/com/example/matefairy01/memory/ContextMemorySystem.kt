package com.example.matefairy01.memory

import android.util.Log
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.ILLMProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.LinkedList

/**
 * 智能上下文管理系统
 * 采用 滑动窗口 (Short-term) + 异步并行压缩缓存 (Long-term) 策略
 */
class ContextMemorySystem(
    private val llmProvider: ILLMProvider,
    private val windowSize: Int = 3
) {
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
     * 组装最终发给 LLM 的对话 Prompt
     */
    fun buildPromptMessages(): List<ChatMessage> {
        val messages = mutableListOf<ChatMessage>()

        // 1. 系统基础设定 (Persona)
        messages.add(ChatMessage(role = "system", content = llmProvider.systemPrompt))

        // 2. 历史摘要缓存 (Long-term)
        if (longTermMemorySummary.isNotEmpty()) {
            messages.add(
                ChatMessage(
                    role = "system", 
                    content = "Previous memory summary: $longTermMemorySummary"
                )
            )
        }

        // 3. 最近 N 轮对话 (Short-term)
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
