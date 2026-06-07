package com.example.matefairy01.orchestrator.ports

/**
 * 对话编排层使用的动作分发端口。
 *
 * 具体动作执行目标由端口适配器负责定位，编排层只保留业务动作意图。
 */
data class ActionDispatchResult(
    val accepted: Boolean,
    val replyOverride: String? = null
) {
    companion object {
        val ACCEPTED = ActionDispatchResult(accepted = true)
    }
}

interface ActionCommandPort {
    suspend fun dispatchAction(intent: String): ActionDispatchResult
}
