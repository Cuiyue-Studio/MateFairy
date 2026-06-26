package com.example.matefairy01.playerinteraction

import android.util.Log
import com.example.matefairy01.interaction.DEFAULT_FAIRY_ACTOR_ID
import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.SceneUpdateContext

enum class PlayerFairyInteractionTrigger {
    HAND_EYE_PINCH,
    CONTROLLER_BUTTON,
    VIRTUAL_HAND_TOUCH,
    DEBUG
}

data class PlayerFairyInteractionRequest(
    val actionId: String,
    val controllerId: String = actionId,
    val subjectId: String = DEFAULT_FAIRY_ACTOR_ID,
    val trigger: PlayerFairyInteractionTrigger,
    val params: Map<String, Any> = emptyMap()
)

/**
 * 玩家-精灵交互期间挂在精灵主体上的 ECS 屏蔽组件。
 * 常驻行为系统只需识别该组件并让出控制权，不参与玩家交互内部调度。
 */
class PlayerFairyInteractionComponent(
    val actionId: String = "",
    val trigger: PlayerFairyInteractionTrigger = PlayerFairyInteractionTrigger.DEBUG
) : Component()

interface PlayerFairyInteractionScheduler {
    fun schedule(request: PlayerFairyInteractionRequest): Boolean

    fun pinchFairy(
        subjectId: String = DEFAULT_FAIRY_ACTOR_ID,
        trigger: PlayerFairyInteractionTrigger = PlayerFairyInteractionTrigger.HAND_EYE_PINCH
    ): Boolean {
        return schedule(
            PlayerFairyInteractionRequest(
                actionId = PinchShakeAngryPlayerFairyActionController.ACTION_ID,
                subjectId = subjectId,
                trigger = trigger
            )
        )
    }

    fun touchFairy(subjectId: String = DEFAULT_FAIRY_ACTOR_ID): Boolean {
        return pinchFairy(
            subjectId = subjectId,
            trigger = PlayerFairyInteractionTrigger.VIRTUAL_HAND_TOUCH
        )
    }
}

interface PlayerFairyInteractionActionController {
    val actionId: String

    fun createInstance(request: PlayerFairyInteractionRequest): PlayerFairyInteractionActionInstance
}

interface PlayerFairyInteractionActionInstance {
    val actionId: String

    fun update(context: SceneUpdateContext): PlayerFairyInteractionActionStatus

    fun cancel() = Unit
}

enum class PlayerFairyInteractionActionStatus {
    RUNNING,
    COMPLETED,
    FAILED
}

class PlayerFairyInteractionRegistry {
    private val controllers = mutableMapOf<String, PlayerFairyInteractionActionController>()

    fun register(controller: PlayerFairyInteractionActionController) {
        controllers[controller.actionId] = controller
        Log.d(TAG, "Registered player-fairy interaction action: ${controller.actionId}")
    }

    fun get(actionId: String): PlayerFairyInteractionActionController? = controllers[actionId]

    private companion object {
        private const val TAG = "PlayerFairyInteractionRegistry"
    }
}

class PlayerFairyInteractionRequestBus {
    private val pendingRequests = ArrayDeque<PlayerFairyInteractionRequest>()

    @Synchronized
    fun enqueue(request: PlayerFairyInteractionRequest): Boolean {
        if (request.actionId.isBlank()) return false
        pendingRequests.addLast(request)
        return true
    }

    @Synchronized
    fun drainLatest(): PlayerFairyInteractionRequest? {
        if (pendingRequests.isEmpty()) return null
        val latest = pendingRequests.last()
        pendingRequests.clear()
        return latest
    }
}

class PlayerFairyInteractionState {
    @Volatile
    var currentInteractionId: String? = null
        private set

    val isActive: Boolean
        get() = currentInteractionId != null

    @Synchronized
    fun start(request: PlayerFairyInteractionRequest) {
        currentInteractionId = request.controllerId
    }

    @Synchronized
    fun finish(controllerId: String) {
        if (currentInteractionId == controllerId) {
            currentInteractionId = null
        }
    }
}

class DefaultPlayerFairyInteractionScheduler(
    private val requestBus: PlayerFairyInteractionRequestBus
) : PlayerFairyInteractionScheduler {
    override fun schedule(request: PlayerFairyInteractionRequest): Boolean {
        return requestBus.enqueue(request)
    }
}

object PlayerFairyInteractionRuntimeDependencies {
    val actionRegistry = PlayerFairyInteractionRegistry()
    val requestBus = PlayerFairyInteractionRequestBus()
    val state = PlayerFairyInteractionState()
    val scheduler: PlayerFairyInteractionScheduler =
        DefaultPlayerFairyInteractionScheduler(requestBus)
}
