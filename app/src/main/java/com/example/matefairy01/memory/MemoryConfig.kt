package com.example.matefairy01.memory

/**
 * 四层记忆系统的运行时配置。
 *
 * 字段含义详见 MEMORY_SYSTEM_DESIGN.md 第八章。
 * 解析路径见 [com.example.matefairy01.config.AppConfigLoader.parseMemoryConfig]。
 */
data class MemoryConfig(
    val consolidate: ConsolidateConfig = ConsolidateConfig(),
    val factIngest: FactIngestConfig = FactIngestConfig(),
    val dream: DreamConfig = DreamConfig(),
    val ingestion: IngestionConfig = IngestionConfig(),
    val retrieval: RetrievalConfig = RetrievalConfig(),
    val embedding: EmbeddingConfig = EmbeddingConfig()
)

/** L1 → L2 触发：累计 token 或轮数兜底 */
data class ConsolidateConfig(
    val tokenThreshold: Int = 3000,
    val turnFallback: Int = 10
)

/** 每轮 FactExtractor 启发式过滤 */
data class FactIngestConfig(
    val minChars: Int = 30,
    val throttleMs: Long = 30_000L
)

/** L4 蒸馏触发与执行预算 */
data class DreamConfig(
    val idleMs: Long = 5 * 60_000L,
    /** 默认 false：onPause 期 Dream 易被 Cached App 杀进程，留下半成品 md */
    val runOnPause: Boolean = false,
    val maxBatchSize: Int = 20,
    val maxIterations: Int = 10,
    /** null 时复用主对话 model；填名字可单独配置（如更便宜的 model 跑 Dream） */
    val modelOverride: String? = null
)

/** IngestionWorker 队列行为 */
data class IngestionConfig(
    /** 单任务超时；onPause 时全队列按此值快速 flush，超时丢回队列 */
    val flushTimeoutPerJobMs: Long = 15_000L
)

/** L2/L3 召回三因子打分 */
data class RetrievalConfig(
    val weightSimilarity: Float = 0.6f,
    val weightRecency: Float = 0.25f,
    val weightImportance: Float = 0.15f,
    val recencyHalfLifeDays: Float = 7f,
    val topKEpisodic: Int = 5,
    val topKFacts: Int = 8
)

/**
 * 远端 embedding 服务配置（OpenAI 兼容协议）。
 *
 * 走 HTTP 调用 `{baseUrl}/embeddings`，请求体兼容 OpenAI：
 *   { "model": "...", "input": "..." }
 * 响应取 `data[0].embedding` 作为向量。
 *
 * 主流 provider（dimension 必须与 model 实际输出严格一致）：
 *   - 智谱 BigModel `embedding-3` baseUrl=https://open.bigmodel.cn/api/paas/v4 dim=1024 或 2048
 *   - 阿里通义 `text-embedding-v3` baseUrl=https://dashscope.aliyuncs.com/compatible-mode/v1 dim=1024
 *   - 火山引擎 `doubao-embedding-text-240715` baseUrl=https://ark.cn-beijing.volces.com/api/v3 dim=2048
 *   - OpenAI `text-embedding-3-small` baseUrl=https://api.openai.com/v1 dim=1536
 *   - 百度千帆 `embedding-v1` baseUrl=https://qianfan.baidubce.com/v2 dim=384
 */
data class EmbeddingConfig(
    /** 仅做记账用途，目前只有 openai_compatible 一种实现 */
    val provider: String = "openai_compatible",
    /** 不带尾部 slash，例如 https://open.bigmodel.cn/api/paas/v4 */
    val baseUrl: String = "",
    val apiKey: String = "",
    val model: String = "",
    /** 必须与 model 实际输出维度严格一致；启动时断言 */
    val dimension: Int = 1024,
    /** 输入文本截断长度（按字符粗截，避免请求体过大） */
    val maxLength: Int = 512,
    val connectTimeoutMs: Long = 5_000L,
    val readTimeoutMs: Long = 15_000L,
    /** Consolidator 等批量入库时单次请求的文本数 */
    val batchSize: Int = 16
)
