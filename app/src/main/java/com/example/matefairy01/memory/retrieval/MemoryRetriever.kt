package com.example.matefairy01.memory.retrieval

import android.util.Log
import com.example.matefairy01.memory.episodic.EpisodicStore
import com.example.matefairy01.memory.semantic.SemanticStore
import com.example.matefairy01.ml.IEmbedder
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 把 L2 episodic + L3 facts 召回结果拼成可注入到 prompt 的文本块。
 *
 * 输出示例：
 * ```
 * # 关于这位用户
 * - [偏好] 用户喜欢喝美式咖啡（5 天前更新，置信 0.9）
 * - [习惯] 每天晚饭后散步（3 天前，0.8）
 *
 * # 过往会话片段
 * - [3 天前] 我们讨论了上海几家精品咖啡店
 * - [昨天] 用户提到周末打算去登山
 * ```
 *
 * 只返回非空段落，调用方决定是否插入到 system prompt。
 */
class MemoryRetriever(
    val episodicStore: EpisodicStore,
    val semanticStore: SemanticStore,
    private val embedder: IEmbedder,
    private val recallTimeoutMs: Long = 1500L
) {
    companion object {
        private const val TAG = "MemoryRetriever"
        private val DATE_FMT = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    }

    /**
     * 按 [query] 召回 facts + episodic，组装成 markdown 文本块。
     *
     * 性能保证（关键路径，回复前必经）：
     * - query 只 embedding **一次**，向量同时喂给 facts + episodic 检索；
     * - 两个检索 **并行** 执行；
     * - 整体包 [recallTimeoutMs] 超时，超时即返回空召回，**绝不阻塞回复**。
     *
     * 失败 / 召回为空 / 超时时返回空串，调用方应判空后再注入 prompt。
     */
    suspend fun assembleContext(query: String): String {
        if (query.isBlank()) return ""

        val (factsResult, episodesResult) = try {
            withTimeout(recallTimeoutMs) {
                // 1. 一次 embedding，复用给两个检索
                val qVec = runCatching { embedder.encode(query) }
                    .onFailure { Log.w(TAG, "embed query failed: ${it.message}") }
                    .getOrNull()

                // 2. facts + episodic 并行
                coroutineScope {
                    val factsDeferred = async {
                        runCatching { semanticStore.searchFactsByVector(qVec) }
                            .onFailure { Log.w(TAG, "searchFacts failed: ${it.message}") }
                            .getOrDefault(emptyList())
                    }
                    val episodesDeferred = async {
                        runCatching { episodicStore.searchByVector(qVec) }
                            .onFailure { Log.w(TAG, "searchEpisodic failed: ${it.message}") }
                            .getOrDefault(emptyList())
                    }
                    Pair(factsDeferred.await(), episodesDeferred.await())
                }
            }
        } catch (e: TimeoutCancellationException) {
            Log.w(TAG, "memory recall timeout (${recallTimeoutMs}ms), skip recall")
            return ""
        }

        val builder = StringBuilder()

        if (factsResult.isNotEmpty()) {
            builder.appendLine("# 关于这位用户")
            for (scored in factsResult) {
                val fact = scored.item
                val ago = humanizeAgo(System.currentTimeMillis() - fact.updatedAt)
                builder.appendLine(
                    "- [${fact.category}] ${fact.content.trim()}（$ago，置信 ${"%.2f".format(fact.confidence)}）"
                )
            }
            builder.appendLine()
        }

        if (episodesResult.isNotEmpty()) {
            builder.appendLine("# 过往会话片段")
            for (scored in episodesResult) {
                val entry = scored.item
                val ago = humanizeAgo(System.currentTimeMillis() - entry.timestamp)
                builder.appendLine("- [$ago] ${entry.content.trim()}")
            }
        }

        return builder.toString().trim()
    }

    private fun humanizeAgo(deltaMs: Long): String {
        if (deltaMs < 0L) return "刚刚"
        val mins = deltaMs / 60_000L
        if (mins < 1L) return "刚刚"
        if (mins < 60L) return "${mins} 分钟前"
        val hours = mins / 60L
        if (hours < 24L) return "${hours} 小时前"
        val days = hours / 24L
        if (days < 14L) return "${days} 天前"
        // 超过两周直接给日期
        return DATE_FMT.format(Date(System.currentTimeMillis() - deltaMs))
    }
}
