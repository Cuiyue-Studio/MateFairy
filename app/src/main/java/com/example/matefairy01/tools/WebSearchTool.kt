package com.example.matefairy01.tools

import android.util.Log
import com.example.matefairy01.config.WebSearchConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

/**
 * 联网搜索本地工具。仿 NAVI 的 web_search：支持多 provider，缺 key 时自动降级到 DuckDuckGo。
 *
 *  - brave / tavily：调用对应 API，需要 apiKey；
 *  - duckduckgo：免 key，抓取 html.duckduckgo.com 结果页（免费但解析较脆弱，作为兜底）。
 */
class WebSearchTool(
    private val config: WebSearchConfig
) : LocalTool {
    companion object {
        private const val TAG = "WebSearchTool"
        private const val UA =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    /** 单条搜索结果 */
    private data class SearchResult(val title: String, val url: String, val snippet: String)

    override val name: String = "web_search"

    override val description: String =
        "联网搜索引擎。输入查询关键词，返回若干条网页标题、URL 和摘要。" +
            "用于获取实时信息、最新新闻、事实查证等模型内部不掌握的内容。"

    override val parameters: JSONObject = JSONObject().apply {
        put("type", "object")
        put("properties", JSONObject().apply {
            put("query", JSONObject().apply {
                put("type", "string")
                put("description", "搜索关键词")
            })
            put("count", JSONObject().apply {
                put("type", "integer")
                put("description", "返回结果数量，范围 1-10")
            })
        })
        put("required", JSONArray().put("query"))
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
        .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
        .build()

    override suspend fun execute(arguments: JSONObject): String = withContext(Dispatchers.IO) {
        val query = arguments.optString("query").trim()
        if (query.isEmpty()) return@withContext "[ERROR] query 不能为空"

        val n = arguments.optInt("count", config.maxResults).coerceIn(1, 10)
        val provider = config.provider.trim().lowercase().ifEmpty { "duckduckgo" }

        runCatching {
            when (provider) {
                "brave" -> searchBrave(query, n)
                "tavily" -> searchTavily(query, n)
                else -> searchDuckDuckGo(query, n)
            }
        }.getOrElse { e ->
            Log.w(TAG, "search failed: ${e.message}")
            "[ERROR] 搜索失败: ${e.message}"
        }
    }

    // ---------------- Brave ----------------

    private fun searchBrave(query: String, n: Int): String {
        val key = config.apiKey
        if (key.isBlank()) {
            Log.w(TAG, "Brave apiKey 缺失，降级到 DuckDuckGo")
            return searchDuckDuckGo(query, n)
        }
        val url = "https://api.search.brave.com/res/v1/web/search?q=${enc(query)}&count=$n"
        val req = Request.Builder()
            .url(url)
            .addHeader("Accept", "application/json")
            .addHeader("X-Subscription-Token", key)
            .get()
            .build()

        httpClient.newCall(req).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) return "[ERROR] Brave HTTP ${resp.code}: ${body.take(200)}"
            val results = JSONObject(body).optJSONObject("web")?.optJSONArray("results")
                ?: return "无结果: $query"
            val items = (0 until results.length()).mapNotNull { results.optJSONObject(it) }.map {
                SearchResult(it.optString("title"), it.optString("url"), it.optString("description"))
            }
            return formatResults(query, items, n)
        }
    }

    // ---------------- Tavily ----------------

    private fun searchTavily(query: String, n: Int): String {
        val key = config.apiKey
        if (key.isBlank()) {
            Log.w(TAG, "Tavily apiKey 缺失，降级到 DuckDuckGo")
            return searchDuckDuckGo(query, n)
        }
        val payload = JSONObject().apply {
            put("query", query)
            put("max_results", n)
        }
        val req = Request.Builder()
            .url("https://api.tavily.com/search")
            .addHeader("Authorization", "Bearer $key")
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        httpClient.newCall(req).execute().use { resp ->
            val body = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) return "[ERROR] Tavily HTTP ${resp.code}: ${body.take(200)}"
            val results = JSONObject(body).optJSONArray("results") ?: return "无结果: $query"
            val items = (0 until results.length()).mapNotNull { results.optJSONObject(it) }.map {
                SearchResult(it.optString("title"), it.optString("url"), it.optString("content"))
            }
            return formatResults(query, items, n)
        }
    }

    // ---------------- DuckDuckGo（免 key 兜底）----------------

    private fun searchDuckDuckGo(query: String, n: Int): String {
        val form = FormBody.Builder().add("q", query).build()
        val req = Request.Builder()
            .url("https://html.duckduckgo.com/html/")
            .addHeader("User-Agent", UA)
            .post(form)
            .build()

        httpClient.newCall(req).execute().use { resp ->
            val html = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) return "[ERROR] DuckDuckGo HTTP ${resp.code}"
            val items = parseDuckDuckGoHtml(html)
            if (items.isEmpty()) return "无结果: $query"
            return formatResults(query, items, n)
        }
    }

    private val linkRegex =
        Regex("""<a[^>]+class="result__a"[^>]*href="([^"]+)"[^>]*>(.*?)</a>""", RegexOption.DOT_MATCHES_ALL)
    private val snippetRegex =
        Regex("""class="result__snippet"[^>]*>(.*?)</a>""", RegexOption.DOT_MATCHES_ALL)
    private val uddgRegex = Regex("""uddg=([^&]+)""")

    private fun parseDuckDuckGoHtml(html: String): List<SearchResult> {
        val links = linkRegex.findAll(html).toList()
        val snippets = snippetRegex.findAll(html).map { clean(it.groupValues[1]) }.toList()
        return links.mapIndexed { i, m ->
            SearchResult(
                title = clean(m.groupValues[2]),
                url = decodeDdgUrl(m.groupValues[1]),
                snippet = snippets.getOrElse(i) { "" }
            )
        }
    }

    private fun decodeDdgUrl(href: String): String {
        val m = uddgRegex.find(href) ?: return if (href.startsWith("//")) "https:$href" else href
        return runCatching { URLDecoder.decode(m.groupValues[1], "UTF-8") }.getOrDefault(href)
    }

    // ---------------- 公共 ----------------

    private fun formatResults(query: String, items: List<SearchResult>, n: Int): String {
        if (items.isEmpty()) return "无结果: $query"
        val sb = StringBuilder("搜索结果: $query\n")
        items.take(n).forEachIndexed { i, r ->
            sb.append("\n${i + 1}. ${clean(r.title)}\n   ${r.url}")
            val sn = clean(r.snippet)
            if (sn.isNotEmpty()) sb.append("\n   $sn")
        }
        return sb.toString()
    }

    private fun clean(s: String): String =
        s.replace(Regex("<[^>]+>"), "")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#x27;", "'")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    private fun enc(s: String): String = URLEncoder.encode(s, "UTF-8")
}
