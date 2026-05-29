package com.example.matefairy01.ml

/**
 * 端侧文本 embedder 抽象。
 *
 * 默认实现 [EmbedderOnnx] 采用 ONNX Runtime + HuggingFace tokenizer。
 * 模型不存在时降级为零向量（[FallbackZeroEmbedder]），保证主对话流不崩。
 */
interface IEmbedder {
    /** 输出维度，例如 bge-small-zh-v1.5 = 512 */
    val dimension: Int

    /** 模型是否就绪。false 时 [encode] 会返回零向量 */
    val isReady: Boolean

    /** 阻塞预热，失败不抛异常，返回 false 表示降级 */
    suspend fun warmup(): Boolean

    /** 单条文本编码，输出已 L2 normalize 的向量；失败返回零向量 */
    suspend fun encode(text: String): FloatArray

    /** 批量编码，默认串行调用 [encode]；后续可优化为单次推理 */
    suspend fun encodeBatch(texts: List<String>): List<FloatArray> =
        texts.map { encode(it) }

    /** 释放原生资源 */
    fun close() {}
}
