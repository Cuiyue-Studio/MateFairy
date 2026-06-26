package com.example.matefairy01.action.handlers

import com.example.matefairy01.action.IActionHandler
import com.example.matefairy01.animation.AnimationConfig
import com.example.matefairy01.animation.AnimationController
import com.example.matefairy01.behavior.FairySemanticResidenceRuntime
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * 通用的非任务型动画处理器
 * 只要在 AnimationConfig 中配置了 NON_TASK_ACTION 类型的意图，
 * 就可以由该 Handler 统一接管，不再需要为每个动作手写 Handler。
 */
class GenericAnimationHandler(
    override val intent: String,
    private val animationController: AnimationController
) : IActionHandler {
    
    override suspend fun execute(params: Map<String, Any>) {
        val anim = AnimationConfig.getAnimationByAction(intent)
        // #region debug-point A:generic-animation-handler
        debugChairChain("A", "GenericAnimationHandler.kt:execute", "GenericAnimationHandler execute", "intent" to intent, "hasAnimation" to (anim != null), "params" to params.toString())
        // #endregion
        if (anim != null) {
            FairySemanticResidenceRuntime.clear("instruction-animation:$intent")
            animationController.playAnimation(anim)
        }
    }
}

// #region debug-point A:generic-animation-reporter
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
