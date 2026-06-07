package com.example.matefairy01.memory.ingestion

import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.memory.FactIngestConfig

/**
 * 启发式过滤：决定一段最近的对话是否值得交给 [FactExtractor]。
 *
 * 抄 NAVI 的 should_ingest：
 * - 太短不抽（闲聊）
 * - 节流窗口内不抽（避免每轮都跑 LLM）
 * - 仅看消息总字数 + 节流；P2 可加更多启发（关键词、问号比例等）
 *
 * 不是单例：每个 worker 持有一个实例，独立维护 [lastIngestTime]。
 */
class ShouldIngest(private val config: FactIngestConfig) {

    @Volatile
    private var lastIngestTime: Long = 0L

    fun check(messages: List<ChatMessage>, now: Long = System.currentTimeMillis()): Boolean {
        if (messages.size < 2) return false
        val totalChars = messages.sumOf { it.content.length }
        if (totalChars < config.minChars) return false
        if (now - lastIngestTime < config.throttleMs) return false
        return true
    }

    fun markIngested(now: Long = System.currentTimeMillis()) {
        lastIngestTime = now
    }

    fun reset() {
        lastIngestTime = 0L
    }
}
