package com.example.matefairy01.memory.semantic

import android.util.Log
import com.example.matefairy01.memory.MemoryConfig
import com.example.matefairy01.memory.episodic.Scored
import com.example.matefairy01.memory.retrieval.RecencyScorer
import com.example.matefairy01.ml.IEmbedder
import com.example.matefairy01.ml.VectorMath

/**
 * L3 语义记忆的高层 API。
 */
class SemanticStore(
    private val factDao: FactDao,
    private val tripleDao: TripleDao,
    private val embedder: IEmbedder,
    private val memoryConfig: MemoryConfig,
    private val recencyScorer: RecencyScorer
) {
    companion object {
        private const val TAG = "SemanticStore"

        /** 候选预过滤上限，N < 2000 时暴力 cosine 仍足够快 */
        private const val DEFAULT_CANDIDATES_LIMIT = 500
    }

    private val retrievalConfig get() = memoryConfig.retrieval

    /**
     * 写入新事实。embedding 失败时存零向量占位（后续召回会被 cosine=0 自然过滤）。
     */
    suspend fun addFact(
        category: String,
        content: String,
        confidence: Float = 0.8f,
        now: Long = System.currentTimeMillis()
    ): Long {
        val vec = runCatching { embedder.encode(content) }.getOrNull()
            ?: FloatArray(embedder.dimension)
        val safeVec = if (vec.size == embedder.dimension) vec else FloatArray(embedder.dimension)
        val embeddingBytes = VectorMath.toBytes(safeVec)

        val fact = Fact(
            category = category,
            content = content,
            confidence = confidence.coerceIn(0f, 1f),
            embedding = embeddingBytes,
            createdAt = now,
            updatedAt = now
        )
        val id = factDao.insert(fact)
        Log.d(TAG, "addFact #$id [$category] $content")
        return id
    }

    /** 用新事实替代旧事实；不删除旧记录，仅打 supersededBy 标记。 */
    suspend fun supersede(oldId: Long, newId: Long, now: Long = System.currentTimeMillis()): Boolean {
        val updated = factDao.supersede(oldId, newId, now) > 0
        if (updated) Log.d(TAG, "supersede #$oldId → #$newId")
        return updated
    }

    /**
     * 三因子事实检索（仅活跃事实）：相似度 + recency + confidence。
     * 与 EpisodicStore 不同，importance 维度由 [Fact.confidence] 取代。
     *
     * @param category 可选类别预过滤；null 时跨类查询
     */
    suspend fun searchFacts(
        query: String,
        topK: Int = retrievalConfig.topKFacts,
        category: String? = null
    ): List<Scored<Fact>> {
        if (query.isBlank() || topK <= 0) return emptyList()
        val qVec = runCatching { embedder.encode(query) }.getOrNull()
        return searchFactsByVector(qVec, topK, category)
    }

    /**
     * 与 [searchFacts] 相同，但接收**已算好的 query 向量**，避免重复 embedding 网络调用。
     * 供 [com.example.matefairy01.memory.retrieval.MemoryRetriever] 一次编码、多处复用。
     */
    suspend fun searchFactsByVector(
        qVec: FloatArray?,
        topK: Int = retrievalConfig.topKFacts,
        category: String? = null
    ): List<Scored<Fact>> {
        if (topK <= 0) return emptyList()

        val now = System.currentTimeMillis()
        val candidates = factDao.listCandidates(category, DEFAULT_CANDIDATES_LIMIT)
        if (candidates.isEmpty()) return emptyList()

        val hasQueryVec = qVec != null && qVec.size == embedder.dimension && hasNonZero(qVec)

        val ws = retrievalConfig.weightSimilarity
        val wr = retrievalConfig.weightRecency
        val wi = retrievalConfig.weightImportance

        return candidates.asSequence()
            .map { fact ->
                val sim = if (hasQueryVec && fact.embedding.size == embedder.dimension * 4) {
                    val v = VectorMath.fromBytes(fact.embedding, embedder.dimension)
                    VectorMath.cosine(qVec!!, v).coerceIn(0f, 1f)
                } else {
                    0f
                }
                val rec = recencyScorer.score(now, fact.updatedAt).coerceIn(0f, 1f)
                val imp = fact.confidence.coerceIn(0f, 1f)
                val total = ws * sim + wr * rec + wi * imp
                Scored(fact, total, sim, rec, imp)
            }
            .sortedByDescending { it.score }
            .take(topK)
            .toList()
    }

    suspend fun listActiveFacts(limit: Int = 100): List<Fact> = factDao.listActive(limit)

    suspend fun listActiveByCategory(category: String, limit: Int = 20): List<Fact> =
        factDao.listActiveByCategory(category, limit)

    suspend fun countFacts(): Int = factDao.count()

    suspend fun countActiveFacts(): Int = factDao.countActive()

    suspend fun findFact(id: Long): Fact? = factDao.findById(id)

    // ----- triples（P1 schema 留位，最小 CRUD） -----

    suspend fun addTriple(
        subject: String,
        predicate: String,
        obj: String,
        confidence: Float = 0.7f,
        sourceEpisodeId: Long? = null,
        now: Long = System.currentTimeMillis()
    ): Long {
        return tripleDao.insert(
            KnowledgeTriple(
                subject = subject,
                predicate = predicate,
                obj = obj,
                confidence = confidence.coerceIn(0f, 1f),
                sourceEpisodeId = sourceEpisodeId,
                createdAt = now
            )
        )
    }

    suspend fun findTriplesBySubject(subject: String, limit: Int = 20): List<KnowledgeTriple> =
        tripleDao.findBySubject(subject, limit)

    suspend fun countTriples(): Int = tripleDao.count()

    /** 删库（仅供 debug / 单测使用） */
    suspend fun deleteAllFacts(): Int = factDao.deleteAll()
    suspend fun deleteAllTriples(): Int = tripleDao.deleteAll()

    private fun hasNonZero(vec: FloatArray): Boolean {
        for (v in vec) if (v != 0f) return true
        return false
    }
}
