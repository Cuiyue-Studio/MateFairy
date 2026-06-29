package com.example.matefairy01.action.handlers

import com.example.matefairy01.action.IActionHandler
import com.example.matefairy01.behavior.FairySemanticResidenceRuntime
import com.example.matefairy01.interaction.DEFAULT_FAIRY_ACTOR_ID
import com.example.matefairy01.interaction.InteractionActionRequest
import com.example.matefairy01.interaction.InteractionActionRequestBus
import com.example.matefairy01.interaction.InteractionActionSource

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
        requestBus.enqueue(
            request
        )
    }

    private fun parseObjectIds(value: Any?): List<String>? {
        return when (value) {
            is String -> listOf(value)
            is List<*> -> value.filterIsInstance<String>().takeIf { it.isNotEmpty() }
            else -> null
        }
    }
}
