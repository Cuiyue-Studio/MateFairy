package com.example.matefairy01.orchestrator.adapters

import com.example.matefairy01.action.ActionRegistry
import com.example.matefairy01.orchestrator.ports.ActionCommandPort

class ActionRegistryPortAdapter(
    private val actionRegistry: ActionRegistry
) : ActionCommandPort {
    override suspend fun dispatchAction(intent: String) {
        actionRegistry.dispatchAction(intent)
    }
}
