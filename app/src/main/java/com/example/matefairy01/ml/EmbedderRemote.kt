package com.example.matefairy01.ml

import android.util.Log
import com.example.matefairy01.memory.EmbeddingConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 远端 embedding 实现，走 OpenAI 兼容协议 `POST {baseUrl}/embeddings`。
 *
 * 设计要点：
 * 1. 配置缺失（baseUrl / apiKey / model 任一为空）→ 进入 stub 模式，返回零向量并打 warning，
 *    保证 MateFairy 在没填 key 时也能跑。
 * 2. 网络失败 / 鉴权失败 / 维度错位 → 返回零向量降级，主对话流不挂。
 * 3. 单条调用同步串行；批量场景由 [encodeBatch] 单请求多 input。
 */
class EmbedderRemote(
    private val config: EmbeddingConfig
) : IEmbedder {

    companion object {
        private const val TAG = "EmbedderRemote"
        private val JSON = "application/json; charset=utf-8".toMediaType()
    }

    override val dimension: Int = config.dimension

    private val stubMode: Boolean =
        config.baseUrl.isBlank() || config.apiKey.isBlank() || config.model.isBlank()

    @Volatile
    private var ready: Boolean = !stubMode
    override val isReady: Boolean get() = ready

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
            .build()
    }

    private val endpoint: String by lazy { "${config.baseUrl.trimEnd('/')}/embeddings" }

    override suspend fun warmup(): Boolean {
        if (stubMode) {
            Log.w(
                TAG,
                "Embedder is in STUB mode (baseUrl/apiKey/model missing). All encode() will return zero vectors."
            )
            return false
        }
        // 跑一次最小请求验证连通；失败不抛
        return try {
            val v = encodeInternal(listOf("warmup")).first()
            ready = v.size == dimension
            if (!ready) {
                Log.w(TAG, "Embedder warmup dim mismatch: got=${v.size}, expect=$dimension")
            } else {
                Log.i(TAG, "Embedder warmup ok. provider=${config.provider} model=${config.model} dim=$dimension")
            }
            ready
        } catch (e: Throwable) {
            Log.w(TAG, "Embedder warmup failed, falling back to zero vector. cause=${e.message}", e)
            ready = false
            false
        }
    }

    override suspend fun encode(text: String): FloatArray {
        if (stubMode) return FloatArray(dimension)
        return try {
            val truncated = if (text.length <= config.maxLength) text else text.substring(0, config.maxLength)
            encodeInternal(listOf(truncated)).first()
        } catch (e: Throwable) {
            Log.w(TAG, "encode failed for text='${text.take(20)}…', returning zero vec. cause=${e.message}")
            FloatArray(dimension)
        }
    }

    override suspend fun encodeBatch(texts: List<String>): List<FloatArray> {
        if (stubMode || texts.isEmpty()) return texts.map { FloatArray(dimension) }
        val batchSize = config.batchSize.coerceAtLeast(1)
        val out = ArrayList<FloatArray>(texts.size)
        for (i in texts.indices step batchSize) {
            val slice = texts.subList(i, minOf(i + batchSize, texts.size))
                .map { if (it.length <= config.maxLength) it else it.substring(0, config.maxLength) }
            try {
                out.addAll(encodeInternal(slice))
            } catch (e: Throwable) {
                Log.w(TAG, "encodeBatch slice failed, fallback zero vec. cause=${e.message}")
                repeat(slice.size) { out.add(FloatArray(dimension)) }
            }
        }
        return out
    }

    // ------------------------------------------------------------------

    private suspend fun encodeInternal(texts: List<String>): List<FloatArray> = withContext(Dispatchers.IO) {
        val body = JSONObject().apply {
            put("model", config.model)
            // OpenAI 协议：input 可以是字符串或字符串数组，单条用字符串便于 provider 兼容性
            if (texts.size == 1) put("input", texts.first())
            else put("input", JSONArray().apply { texts.forEach { put(it) } })
        }.toString()

        val request = Request.Builder()
            .url(endpoint)
            .header("Authorization", "Bearer ${config.apiKey}")
            .header("Content-Type", "application/json")
            .post(body.toRequestBody(JSON))
            .build()

        val responseText = client.newCall(request).awaitString()
        parseEmbeddings(responseText, expectCount = texts.size)
    }

    /**
     * 解析 OpenAI 兼容响应：
     *   { "data": [ { "embedding": [...] }, ... ], "model": "...", "usage": ... }
     * 维度与 [dimension] 不一致时抛 [IllegalStateException]，由上层降级零向量。
     */
    private fun parseEmbeddings(responseText: String, expectCount: Int): List<FloatArray> {
        val root = JSONObject(responseText)
        val dataArr = root.optJSONArray("data")
            ?: throw IllegalStateException("response missing 'data' field: ${responseText.take(200)}")

        val out = ArrayList<FloatArray>(dataArr.length())
        for (i in 0 until dataArr.length()) {
            val item = dataArr.getJSONObject(i)
            val emb = item.getJSONArray("embedding")
            val vec = FloatArray(emb.length()) { idx -> emb.getDouble(idx).toFloat() }
            if (vec.size != dimension) {
                throw IllegalStateException("dim mismatch: got=${vec.size} expect=$dimension")
            }
            out.add(vec)
        }
        if (out.size != expectCount) {
            throw IllegalStateException("count mismatch: got=${out.size} expect=$expectCount")
        }
        return out
    }
}

/** OkHttp 协程化：把 enqueue 包成 suspend，取消时同步取消请求。 */
private suspend fun Call.awaitString(): String = suspendCancellableCoroutine { cont ->
    enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            cont.resumeWithException(e)
        }

        override fun onResponse(call: Call, response: Response) {
            response.use {
                if (!it.isSuccessful) {
                    cont.resumeWithException(IOException("HTTP ${it.code}: ${it.body?.string()?.take(200)}"))
                    return
                }
                val text = it.body?.string().orEmpty()
                cont.resume(text)
            }
        }
    })
    cont.invokeOnCancellation { runCatching { cancel() } }
}
