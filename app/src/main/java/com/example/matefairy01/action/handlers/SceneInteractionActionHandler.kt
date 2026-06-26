package com.example.matefairy01.action.handlers

import com.example.matefairy01.action.IActionHandler
import com.example.matefairy01.behavior.FairySemanticResidenceRuntime
import com.example.matefairy01.interaction.DEFAULT_FAIRY_ACTOR_ID
import com.example.matefairy01.interaction.InteractionActionRequest
import com.example.matefairy01.interaction.InteractionActionRequestBus
import com.example.matefairy01.interaction.InteractionActionSource
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * 将 LLM/传统意图分发桥接到 ECS 场景内的持续交互 action。
 */
class SceneInteractionActionHandler(
    override val intent: String,
    private val requestBus: InteractionActionRequestBus,
    private val defaultSubjectId: String = DEFAULT_FAIRY_ACTOR_ID,
    private val defaultObjectIds: List<String> = emptyList()
) : IActionHandler {
    override suspend fun execute(params: Map<String, Any>) {
        FairySemanticResidenceRuntime.clear("instruction-action:$intent")
        val request = InteractionActionRequest(
            actionId = intent,
            subjectId = params["subjectId"] as? String ?: defaultSubjectId,
            objectIds = parseObjectIds(params["objectIds"]) ?: defaultObjectIds,
            params = params,
            source = InteractionActionSource.DIALOGUE
        )
        val enqueued = requestBus.enqueue(
            request
        )
        // #region debug-point B:scene-interaction-handler
        debugChairChain("B", "SceneInteractionActionHandler.kt:execute", "SceneInteractionActionHandler enqueue", "intent" to intent, "enqueued" to enqueued, "subjectId" to request.subjectId, "objectIds" to request.objectIds.joinToString("|"))
        // #endregion
    }

    private fun parseObjectIds(value: Any?): List<String>? {
        return when (value) {
            is String -> listOf(value)
            is List<*> -> value.filterIsInstance<String>().takeIf { it.isNotEmpty() }
            else -> null
        }
    }
}

// #region debug-point B:scene-interaction-reporter
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
