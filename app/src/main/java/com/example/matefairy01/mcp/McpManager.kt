package com.example.matefairy01.mcp

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject

/**
 * 管理多个 MCP Server 客户端，统一聚合工具，并按 wrappedName 路由调用。
 *
 * 使用方式：
 *   val manager = McpManager(listOf(cfg1, cfg2))
 *   manager.ensureInitialized()                    // 启动时（或首次 chat 时）调用
 *   val toolsJson = manager.buildToolsJsonForOpenAI()  // 喂给 DeepSeek 的 tools 字段
 *   manager.callTool(wrappedName, argsJson)        // 模型选定工具后路由调用
 */
class McpManager(
    configs: List<McpServerConfig>
) {
    companion object {
        private const val TAG = "McpManager"
    }

    private val clients: Map<String, McpClient> =
        configs.associate { it.name to McpClient(it) }

    private val initMutex = Mutex()

    @Volatile
    private var initialized: Boolean = false

    /** 是否有任何配置过的 MCP Server */
    val hasAnyServer: Boolean get() = clients.isNotEmpty()

    /**
     * 并发初始化所有 MCP Server，单个失败不影响其它。幂等。
     */
    suspend fun ensureInitialized() {
        if (initialized || clients.isEmpty()) {
            initialized = true
            return
        }
        initMutex.withLock {
            if (initialized) return

            coroutineScope {
                clients.values.map { client ->
                    async {
                        runCatching { client.ensureInitialized() }
                            .onFailure {
                                Log.w(
                                    TAG,
                                    "MCP server '${client.serverName}' init failed: ${it.message}"
                                )
                            }
                    }
                }.forEach { it.await() }
            }
            initialized = true
        }
    }

    /**
     * 返回所有已连接 server 的工具集合
     */
    fun allTools(): List<McpTool> =
        clients.values.flatMap { it.tools }

    /**
     * 是否存在任何已就绪工具（影响 LLM 是否进入 function calling 流程）
     */
    fun hasAvailableTools(): Boolean = allTools().isNotEmpty()

    /**
     * 构造 OpenAI / DeepSeek function calling 接口需要的 tools 数组
     */
    fun buildToolsJsonForOpenAI(): JSONArray {
        val arr = JSONArray()
        for (tool in allTools()) {
            arr.put(
                JSONObject().apply {
                    put("type", "function")
                    put(
                        "function",
                        JSONObject().apply {
                            put("name", tool.wrappedName)
                            put("description", tool.description)
                            put("parameters", tool.inputSchema)
                        }
                    )
                }
            )
        }
        return arr
    }

    /**
     * 按 wrappedName 路由调用一个工具
     */
    suspend fun callTool(wrappedName: String, arguments: JSONObject): String {
        val tool = allTools().firstOrNull { it.wrappedName == wrappedName }
            ?: return "[ERROR] tool '$wrappedName' not found"
        val client = clients[tool.serverName]
            ?: return "[ERROR] server '${tool.serverName}' not registered"
        return try {
            client.callTool(tool.rawName, arguments)
        } catch (e: Exception) {
            Log.w(TAG, "callTool '$wrappedName' failed: ${e.message}")
            "[ERROR] ${e.javaClass.simpleName}: ${e.message ?: "unknown"}"
        }
    }
}
