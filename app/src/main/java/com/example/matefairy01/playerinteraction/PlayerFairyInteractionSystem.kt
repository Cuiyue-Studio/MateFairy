package com.example.matefairy01.playerinteraction

import android.util.Log
import com.example.matefairy01.interaction.InteractionActionInterruptReason
import com.example.matefairy01.interaction.InteractionActionRuntimeDependencies
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.System

/**
 * 独立调度“玩家-精灵”交互 action。
 * 新请求会覆盖当前玩家交互，并通过 interrupt bus 让旧的非玩家 action 安全退出。
 */
class PlayerFairyInteractionSystem : System() {
    private var activeControllerId: String? = null
    private var activeAction: PlayerFairyInteractionActionInstance? = null

    override fun update(context: SceneUpdateContext) {
        PlayerFairyInteractionRuntimeDependencies.requestBus.drainLatest()?.let { request ->
            startOrReplace(request)
        }

        val controllerId = activeControllerId ?: return
        val action = activeAction ?: return
        when (action.update(context)) {
            PlayerFairyInteractionActionStatus.RUNNING -> Unit
            PlayerFairyInteractionActionStatus.COMPLETED -> {
                clearActive(controllerId)
                Log.d(TAG, "Player-fairy interaction completed: ${action.actionId}")
            }
            PlayerFairyInteractionActionStatus.FAILED -> {
                action.cancel()
                clearActive(controllerId)
                Log.w(TAG, "Player-fairy interaction failed: ${action.actionId}")
            }
        }
    }

    private fun startOrReplace(request: PlayerFairyInteractionRequest) {
        activeAction?.cancel()
        activeControllerId?.let(PlayerFairyInteractionRuntimeDependencies.state::finish)

        InteractionActionRuntimeDependencies.interruptBus.requestInterrupt(
            InteractionActionInterruptReason.PLAYER_FAIRY_INTERACTION
        )

        val controller = PlayerFairyInteractionRuntimeDependencies.actionRegistry.get(request.actionId)
        if (controller == null) {
            Log.w(TAG, "No player-fairy controller for action=${request.actionId}")
            return
        }

        PlayerFairyInteractionRuntimeDependencies.state.start(request)
        activeControllerId = request.controllerId
        activeAction = controller.createInstance(request)
        Log.d(TAG, "Player-fairy interaction started: ${request.actionId}")
    }

    private fun clearActive(controllerId: String) {
        PlayerFairyInteractionRuntimeDependencies.state.finish(controllerId)
        activeControllerId = null
        activeAction = null
    }

    private companion object {
        private const val TAG = "PlayerFairyInteractionSystem"
    }
}
