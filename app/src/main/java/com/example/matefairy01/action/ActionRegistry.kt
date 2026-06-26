package com.example.matefairy01.action

import android.util.Log
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * 动作注册表，采用策略模式分发动作意图
 */
class ActionRegistry {
    private val handlers = mutableMapOf<String, IActionHandler>()

    /**
     * 注册动作处理器
     */
    fun register(handler: IActionHandler) {
        handlers[handler.intent] = handler
        Log.d("ActionRegistry", "Registered handler for intent: ${handler.intent}")
        // #region debug-point A:action-registry-register
        debugChairChain("A", "ActionRegistry.kt:register", "ActionRegistry register", "intent" to handler.intent, "handler" to handler::class.java.simpleName)
        // #endregion
    }

    /**
     * 移除动作处理器
     */
    fun unregister(intent: String) {
        handlers.remove(intent)
    }

    /**
     * 根据 intent 分发并执行动作
     */
    suspend fun dispatchAction(intent: String, params: Map<String, Any> = emptyMap()) {
        if (intent.isEmpty() || intent == "none") return
        
        val handler = handlers[intent]
        // #region debug-point A:action-registry-dispatch
        debugChairChain("A", "ActionRegistry.kt:dispatchAction", "ActionRegistry dispatch", "intent" to intent, "handler" to (handler?.javaClass?.simpleName ?: "null"), "registered" to handlers.keys.sorted().joinToString("|"))
        // #endregion
        if (handler != null) {
            handler.execute(params)
        } else {
            Log.w("ActionRegistry", "No handler found for intent: $intent")
        }
    }
}

// #region debug-point A:action-registry-reporter
private fun debugChairChain(hypothesisId: String, location: String, msg: String, vararg fields: Pair<String, Any?>) {
    thread(start = true) {
        runCatching {
            val data = fields.joinToString(",") { "\"${it.first}\":\"${it.second.toString().replace("\\", "\\\\").replace("\"", "\\\"")}\"" }
            val body = "{\"sessionId\":\"chair-command-chain\",\"runId\":\"pre-fix\",\"hypothesisId\":\"$hypothesisId\",\"location\":\"$location\",\"msg\":\"[DEBUG] $msg\",\"data\":{$data},\"ts\":${System.currentTimeMillis()}}"
            val connection = (URL("http://10.4.47.36:7777/event").openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 500
                readTimeout = 500
            }
            connection.outputStream.use { it.write(body.toByteArray()) }
            connection.inputStream.close()
            connection.disconnect()
        }
    }
}
// #endregion
