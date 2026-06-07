package com.example.matefairy01.memory.episodic

import android.util.Log
import com.example.matefairy01.memory.MemoryConfig
import com.example.matefairy01.memory.retrieval.RecencyScorer
import com.example.matefairy01.ml.IEmbedder
import com.example.matefairy01.ml.VectorMath

/**
 * L2 情节记忆的高层 API。封装 DAO 与 embedder，避免上层直接耦合 Room 与 ML。
 *
 * 检索路径（[search]）：
 * 1. 时间窗预过滤 [recentCandidatesLimit] 取近段记录
 * 2. 用 query embedding 与每条 entry embedding 算 cosine（暴力，N < 2000 足够快）
 * 3. 三因子加权打分：similarity + recency + importance
 * 4. 按总分倒序取 top-k
 *
 * 写入路径（[add]）：embedder 不可用时存 null，不阻塞主流程。
 */
class EpisodicStore(
    private val dao: EpisodicDao,
    private val embedder: IEmbedder,
    private val memoryConfig: MemoryConfig,
    private val recencyScorer: RecencyScorer
) {
    companion object {
        private const val TAG = "EpisodicStore"

        /** 时间窗预过滤：6 个月 */
        private const val DEFAULT_LOOKBACK_MS = 180L * 24L * 3600L * 1000L

        /** 单次 search 最多扫描的候选条数（保护暴力 cosine 时延） */
        private const val DEFAULT_CANDIDATES_LIMIT = 500
    }

    private val retrievalConfig get() = memoryConfig.retrieval

    /**
     * 添加一条情节记忆。importance 钳制到 1..10。
     * 返回插入后的 rowid。
     */
    suspend fun add(
        content: String,
        timestamp: Long = System.currentTimeMillis(),
        importance: Int = 5,
        sourceTurnRange: String? = null
    ): Long {
        val safeImportance = importance.coerceIn(1, 10)
        val vec = runCatching { embedder.encode(content) }.getOrNull()
        val embeddingBytes = if (vec != null && vec.size == embedder.dimension && hasNonZero(vec)) {
            VectorMath.toBytes(vec)
        } else {
            null
        }

        val entry = EpisodicEntry(
            content = content,
            timestamp = timestamp,
            importance = safeImportance,
            embedding = embeddingBytes,
            sourceTurnRange = sourceTurnRange
        )
        val id = dao.insert(entry)
        Log.d(TAG, "add #$id (importance=$safeImportance, embedded=${embeddingBytes != null})")
        return id
    }

    /**
     * 三因子检索：相似度 × recency × importance 加权。
     *
     * @param query 用户当前提问 / 关键词
     * @param topK 返回 top 多少（默认取 [com.example.matefairy01.memory.RetrievalConfig.topKEpisodic]）
     * @return 按总分倒序的 entry 列表，可能为空
     */
    suspend fun search(query: String, topK: Int = retrievalConfig.topKEpisodic): List<Scored<EpisodicEntry>> {
        if (query.isBlank() || topK <= 0) return emptyList()
        val qVec = runCatching { embedder.encode(query) }.getOrNull()
        return searchByVector(qVec, topK)
    }

    /**
     * 与 [search] 相同，但接收**已算好的 query 向量**，避免重复 embedding 网络调用。
     */
    suspend fun searchByVector(qVec: FloatArray?, topK: Int = retrievalConfig.topKEpisodic): List<Scored<EpisodicEntry>> {
        if (topK <= 0) return emptyList()

        val now = System.currentTimeMillis()
        val candidates = dao.recentSince(
            sinceTimestamp = now - DEFAULT_LOOKBACK_MS,
            limit = DEFAULT_CANDIDATES_LIMIT
        )
        if (candidates.isEmpty()) return emptyList()

        val hasQueryVec = qVec != null && qVec.size == embedder.dimension && hasNonZero(qVec)

        val ws = retrievalConfig.weightSimilarity
        val wr = retrievalConfig.weightRecency
        val wi = retrievalConfig.weightImportance

        return candidates.asSequence()
            .map { entry ->
                val sim = if (hasQueryVec && entry.embedding != null && entry.embedding.size == embedder.dimension * 4) {
                    val v = VectorMath.fromBytes(entry.embedding, embedder.dimension)
                    VectorMath.cosine(qVec!!, v).coerceIn(0f, 1f)
                } else {
                    0f
                }
                val rec = recencyScorer.score(now, entry.timestamp).coerceIn(0f, 1f)
                val imp = (entry.importance / 10f).coerceIn(0f, 1f)
                val total = ws * sim + wr * rec + wi * imp
                Scored(entry, total, sim, rec, imp)
            }
            .sortedByDescending { it.score }
            .take(topK)
            .toList()
    }

    suspend fun count(): Int = dao.count()

    suspend fun findById(id: Long): EpisodicEntry? = dao.findById(id)

    /** 删库（仅供 debug / 单测使用） */
    suspend fun deleteAll(): Int = dao.deleteAll()

    private fun hasNonZero(vec: FloatArray): Boolean {
        for (v in vec) if (v != 0f) return true
        return false
    }
}

/** 携带打分的检索结果，便于上层做二次重排或诊断 */
data class Scored<T>(
    val item: T,
    val score: Float,
    val similarity: Float,
    val recency: Float,
    val importance: Float
)
