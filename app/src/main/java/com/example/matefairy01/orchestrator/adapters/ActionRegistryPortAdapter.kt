package com.example.matefairy01.orchestrator.adapters

import com.example.matefairy01.action.ActionRegistry
import com.example.matefairy01.interaction.PlayFootballActionController
import com.example.matefairy01.interaction.PlayFootballPreconditions
import com.example.matefairy01.orchestrator.ports.ActionDispatchResult
import com.example.matefairy01.orchestrator.ports.ActionCommandPort

class ActionRegistryPortAdapter(
    private val actionRegistry: ActionRegistry
) : ActionCommandPort {
    override suspend fun dispatchAction(intent: String): ActionDispatchResult {
        if (intent == PlayFootballActionController.ACTION_ID && !PlayFootballPreconditions.canActivate()) {
            return ActionDispatchResult(
                accepted = false,
                replyOverride = PlayFootballPreconditions.TOO_FAR_REPLY
            )
        }

        actionRegistry.dispatchAction(intent)
        return ActionDispatchResult.ACCEPTED
    }
}
