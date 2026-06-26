package com.example.matefairy01.mcp

import org.json.JSONObject

/**
 * MCP 传输类型。Android 端只支持基于 HTTP 的传输。
 */
enum class McpTransportType {
    /** Streamable HTTP transport，单次 POST JSON-RPC，响应可为 JSON 或 SSE */
    STREAMABLE_HTTP,
    /** Server-Sent Events 长连接，暂未实现，作为预留 */
    SSE;

    companion object {
        fun fromRaw(raw: String?): McpTransportType {
            return when (raw?.trim()?.lowercase()) {
                "sse" -> SSE
                else -> STREAMABLE_HTTP
            }
        }
    }
}

/**
 * 单个 MCP Server 的配置
 *
 * 对应 app_config.json:
 * ```
 * "mcpServers": {
 *   "my-server": {
 *     "type": "streamableHttp",
 *     "url": "http://192.168.1.100:3000/mcp",
 *     "headers": { "Authorization": "Bearer xxx" },
 *     "enabled": true,
 *     "toolTimeoutMs": 30000,
 *     "enabledTools": ["*"]
 *   }
 * }
 * ```
 */
data class McpServerConfig(
    val name: String,
    val type: McpTransportType = McpTransportType.STREAMABLE_HTTP,
    val url: String,
    val headers: Map<String, String> = emptyMap(),
    val enabled: Boolean = true,
    val connectTimeoutMs: Long = 10_000L,
    val readTimeoutMs: Long = 60_000L,
    val toolTimeoutMs: Long = 30_000L,
    /** 启用的工具列表。["*"] 表示全部启用；空列表表示禁用所有 */
    val enabledTools: List<String> = listOf("*")
)

/**
 * 从远端 tools/list 拉取得到的工具元数据
 */
class McpTool(
    /** 所属 server 配置名（用户在 config 里定义的 key） */
    val serverName: String,
    /** 远端原始工具名 */
    val rawName: String,
    /** 提供给 LLM 的工具名（mcp_<server>_<rawName>，已 sanitize），保证全局唯一 */
    val wrappedName: String,
    val description: String,
    /** JSON Schema 描述的输入参数 */
    val inputSchema: JSONObject
)
