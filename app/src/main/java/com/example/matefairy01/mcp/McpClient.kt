package com.example.matefairy01.mcp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

/**
 * 单个 MCP Server 的 HTTP 客户端
 *
 * 实现 MCP 协议的 streamable-http 传输：
 *  - POST JSON-RPC 2.0 到 server.url
 *  - 响应可能为 application/json 或 text/event-stream (SSE)
 *  - 若 server 返回 Mcp-Session-Id 响应头，后续请求带上
 *
 * 协议参考：https://spec.modelcontextprotocol.io/specification/2024-11-05/
 */
class McpClient(
    private val config: McpServerConfig,
    private val clientName: String = "MateFairy",
    private val clientVersion: String = "1.0"
) {
    companion object {
        private const val TAG = "McpClient"
        private const val PROTOCOL_VERSION = "2024-11-05"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
        .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
        .writeTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)
        .build()

    private val mutex = Mutex()
    private val idCounter = AtomicLong(1)

    @Volatile
    private var initialized: Boolean = false

    @Volatile
    private var sessionId: String? = null

    /** 远端拉取到并过滤后的工具列表 */
    var tools: List<McpTool> = emptyList()
        private set

    val serverName: String get() = config.name

    /**
     * 建立连接：执行 initialize、notifications/initialized、tools/list。
     * 幂等，可被并发调用，内部用 mutex 防重入。
     */
    suspend fun ensureInitialized() {
        if (initialized) return
        mutex.withLock {
            if (initialized) return

            val initParams = JSONObject().apply {
                put("protocolVersion", PROTOCOL_VERSION)
                put("capabilities", JSONObject())
                put(
                    "clientInfo",
                    JSONObject().put("name", clientName).put("version", clientVersion)
                )
            }
            rpcCall("initialize", initParams)

            // notifications/initialized 没有响应体
            sendNotification("notifications/initialized")

            // 拉取工具列表
            val listResult = rpcCall("tools/list", JSONObject())
            tools = parseTools(listResult)

            initialized = true
            Log.i(TAG, "MCP server '${config.name}' connected, ${tools.size} tools available")
        }
    }

    /**
     * 调用一个具体工具，返回展平后的文本结果
     */
    suspend fun callTool(rawName: String, arguments: JSONObject): String {
        ensureInitialized()
        val params = JSONObject().apply {
            put("name", rawName)
            put("arguments", arguments)
        }
        val result = withTimeout(config.toolTimeoutMs) {
            rpcCall("tools/call", params)
        }
        return flattenToolResult(result)
    }

    // ---------- 内部实现 ----------

    private fun parseTools(toolsListResult: JSONObject): List<McpTool> {
        val toolsArray = toolsListResult.optJSONArray("tools") ?: return emptyList()
        val enabledSet = config.enabledTools.toSet()
        val allowAll = "*" in enabledSet

        val result = mutableListOf<McpTool>()
        for (i in 0 until toolsArray.length()) {
            val item = toolsArray.optJSONObject(i) ?: continue
            val rawName = item.optString("name").takeIf { it.isNotBlank() } ?: continue
            val wrapped = sanitizeName("mcp_${config.name}_$rawName")

            if (!allowAll && rawName !in enabledSet && wrapped !in enabledSet) {
                continue
            }

            val desc = item.optString("description", rawName)
            val schema = item.optJSONObject("inputSchema")
                ?: JSONObject().put("type", "object").put("properties", JSONObject())

            result += McpTool(
                serverName = config.name,
                rawName = rawName,
                wrappedName = wrapped,
                description = desc,
                inputSchema = schema
            )
        }
        return result
    }

    private fun flattenToolResult(result: JSONObject): String {
        // MCP tools/call 结果形如 {"content":[{"type":"text","text":"..."}], "isError":false}
        val contentArr = result.optJSONArray("content") ?: return result.toString()
        val parts = mutableListOf<String>()
        for (i in 0 until contentArr.length()) {
            val block = contentArr.optJSONObject(i) ?: continue
            when (block.optString("type")) {
                "text" -> parts += block.optString("text")
                else -> parts += block.toString()
            }
        }
        val joined = parts.joinToString("\n").ifBlank { "(no output)" }
        return if (result.optBoolean("isError")) "[ERROR] $joined" else joined
    }

    /**
     * 发送一个 JSON-RPC 调用，返回 result 字段内容（JSONObject）
     */
    private suspend fun rpcCall(method: String, params: JSONObject): JSONObject =
        withContext(Dispatchers.IO) {
            val id = idCounter.getAndIncrement()
            val payload = JSONObject().apply {
                put("jsonrpc", "2.0")
                put("id", id)
                put("method", method)
                put("params", params)
            }

            val responseText = doHttpPost(payload)
            val responseJson = parseJsonRpcResponse(responseText)

            responseJson.optJSONObject("error")?.let { err ->
                throw IOException(
                    "MCP '${config.name}' $method error: code=${err.optInt("code")} message=${err.optString("message")}"
                )
            }

            responseJson.optJSONObject("result")
                ?: throw IOException("MCP '${config.name}' $method missing result: $responseText")
        }

    private suspend fun sendNotification(method: String) = withContext(Dispatchers.IO) {
        // notification 不带 id，且服务端可能不返回 body
        val payload = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", method)
            put("params", JSONObject())
        }
        try {
            doHttpPost(payload)
        } catch (e: Exception) {
            Log.w(TAG, "Notification $method ignored error: ${e.message}")
        }
    }

    private fun doHttpPost(payload: JSONObject): String {
        val builder = Request.Builder()
            .url(config.url)
            .addHeader("Content-Type", "application/json")
            // 同时接受 JSON 和 SSE 两种响应（MCP 规范要求）
            .addHeader("Accept", "application/json, text/event-stream")
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))

        config.headers.forEach { (k, v) -> builder.addHeader(k, v) }
        sessionId?.let { builder.addHeader("Mcp-Session-Id", it) }

        httpClient.newCall(builder.build()).execute().use { response ->
            if (!response.isSuccessful) {
                val body = response.body?.string().orEmpty().take(300)
                throw IOException(
                    "MCP '${config.name}' HTTP ${response.code} ${response.message} $body"
                )
            }

            // 首次响应可能带 Mcp-Session-Id，记下来
            response.header("Mcp-Session-Id")?.let { sid ->
                if (sessionId == null) {
                    sessionId = sid
                    Log.d(TAG, "MCP '${config.name}' session id: $sid")
                }
            }

            val contentType = response.header("Content-Type").orEmpty().lowercase()
            val body = response.body?.string().orEmpty()

            return if ("text/event-stream" in contentType) {
                extractFirstSseJson(body)
                    ?: throw IOException("MCP '${config.name}' empty SSE response")
            } else {
                body
            }
        }
    }

    /**
     * 从 SSE 响应正文中提取第一条 `data:` JSON。
     *
     * SSE 格式：
     * ```
     * event: message
     * data: {"jsonrpc":"2.0",...}
     *
     * ```
     */
    private fun extractFirstSseJson(body: String): String? {
        val buf = StringBuilder()
        for (line in body.lineSequence()) {
            if (line.startsWith("data:")) {
                if (buf.isNotEmpty()) buf.append('\n')
                buf.append(line.removePrefix("data:").trimStart())
            } else if (line.isBlank() && buf.isNotEmpty()) {
                // 一条 SSE event 结束
                return buf.toString()
            }
        }
        return buf.takeIf { it.isNotEmpty() }?.toString()
    }

    private fun parseJsonRpcResponse(text: String): JSONObject {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            throw IOException("MCP '${config.name}' empty response body")
        }
        return try {
            JSONObject(trimmed)
        } catch (e: Exception) {
            // 兜底：可能是 JSONArray 批量响应，取第一个
            val arr = JSONArray(trimmed)
            arr.optJSONObject(0)
                ?: throw IOException("MCP '${config.name}' unparseable response: ${trimmed.take(300)}")
        }
    }

    private fun sanitizeName(name: String): String {
        // MCP 工具名规范化，去掉模型 API 不接受的字符
        return name.replace(Regex("[^a-zA-Z0-9_-]"), "_").replace(Regex("_+"), "_")
    }
}
