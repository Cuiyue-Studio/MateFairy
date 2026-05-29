package com.example.matefairy01.memory.ingestion

import com.example.matefairy01.ai.ChatMessage

/**
 * 异步入队的写入任务。由 [IngestionWorker] 串行消费。
 *
 * 设计原则：每个 Job 只描述"要做什么"，不持有任何 Store / LLM 引用，
 * 真正的执行依赖由 worker 注入。
 */
sealed class IngestionJob {

    /** 把一段对话片段压缩进 L2 EpisodicStore */
    data class ConsolidateJob(
        val messages: List<ChatMessage>,
        val turnRange: String? = null
    ) : IngestionJob()

    /** 从最近一段对话抽取持久事实进 L3 SemanticStore */
    data class ExtractFactsJob(
        val messages: List<ChatMessage>
    ) : IngestionJob()
}
