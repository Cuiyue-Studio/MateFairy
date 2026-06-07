package com.example.matefairy01.tools

import org.json.JSONObject

/**
 * 进程内本地工具抽象。与远程 MCP 工具并列，统一进入 LLM 的 function calling 流程。
 *
 * 与 [com.example.matefairy01.mcp.McpTool] 的区别：
 *  - MCP 工具通过 HTTP JSON-RPC 调用远程 server；
 *  - LocalTool 直接在 App 进程内执行（如用 OkHttp 调外部 API）。
 *
 * McpManager 会聚合 MCP 工具与本地工具，统一构造 tools 数组并路由调用。
 */
interface LocalTool {
    /** 提供给 LLM 的工具名，需全局唯一且符合 [a-zA-Z0-9_-] */
    val name: String

    /** 工具用途描述，供模型判断何时调用 */
    val description: String

    /** JSON Schema 描述的输入参数 */
    val parameters: JSONObject

    /** 执行工具，返回展平后的文本结果（失败时返回以 "[ERROR]" 开头的字符串） */
    suspend fun execute(arguments: JSONObject): String
}
