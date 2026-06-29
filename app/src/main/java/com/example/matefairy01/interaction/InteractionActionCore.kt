package com.example.matefairy01.interaction

import android.util.Log
import com.example.matefairy01.behavior.FairySemanticResidenceRuntime
import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.EntityQueryCondition
import com.pico.spatial.core.ecs.Scene
import com.pico.spatial.core.ecs.SceneUpdateContext

enum class InteractionActorRole {
    FAIRY,
    VIRTUAL_OBJECT
}

/**
 * 标记可以作为 action 主体的实体。默认主体为精灵。
 */
class InteractionActorComponent(
    val actorId: String = DEFAULT_FAIRY_ACTOR_ID,
    val role: InteractionActorRole = InteractionActorRole.FAIRY
) : Component()

/**
 * 标记可以作为 action 客体的虚拟实体。
 */
class InteractionObjectComponent(
    val objectId: String,
    val tags: Set<String> = emptySet()
) : Component()

/**
 * action 执行期间加在主体上，提醒常驻行为系统让出运动控制权。
 */
class FairyActionLockComponent(
    val actionId: String
) : Component()

data class InteractionActionRequest(
    val actionId: String,
    val controllerId: String = actionId,
    val subjectId: String = DEFAULT_FAIRY_ACTOR_ID,
    val objectIds: List<String> = emptyList(),
    val params: Map<String, Any> = emptyMap(),
    val source: InteractionActionSource = InteractionActionSource.DIALOGUE
)

enum class InteractionActionSource {
    RANDOM,
    DIALOGUE,
    DEBUG
}

enum class InteractionActionStatus {
    RUNNING,
    COMPLETED,
    FAILED
}

enum class InteractionActionInterruptReason {
    PLAYER_FAIRY_INTERACTION
}

interface InteractionActionController {
    val actionId: String

    fun createInstance(request: InteractionActionRequest): InteractionActionInstance
}

interface InteractionActionInstance {
    val actionId: String

    fun update(context: SceneUpdateContext): InteractionActionStatus

    fun onFinished(status: InteractionActionStatus) = Unit

    fun cancel() = Unit
}

interface InteractionActionListener {
    fun onActionStarted(actionId: String, controllerId: String, source: InteractionActionSource) = Unit

    fun onActionFinished(
        actionId: String,
        controllerId: String,
        source: InteractionActionSource,
        status: InteractionActionStatus
    ) = Unit
}

class InteractionActionRegistry {
    private val controllers = mutableMapOf<String, InteractionActionController>()

    fun register(controller: InteractionActionController) {
        controllers[controller.actionId] = controller
        Log.d(TAG, "Registered interaction action: ${controller.actionId}")
    }

    fun get(actionId: String): InteractionActionController? {
        return controllers[actionId]
    }

    private companion object {
        private const val TAG = "InteractionActionRegistry"
    }
}

class InteractionActionRequestBus {
    private val pendingRequests = ArrayDeque<InteractionActionRequest>()

    @Synchronized
    fun enqueue(request: InteractionActionRequest): Boolean {
        if (request.actionId.isBlank()) {
            return false
        }
        if (request.source == InteractionActionSource.RANDOM && FairySemanticResidenceRuntime.isActive()) {
            Log.d(TAG, "Ignore random action=${request.actionId}; fairy is in semantic residence")
            return false
        }
        val lockState = InteractionActionRuntimeDependencies.lockState
        if (lockState.isLocked && !lockState.canPreempt(request)) {
            Log.d(TAG, "Ignore action=${request.actionId}; action lock is active")
            return false
        }
        pendingRequests.addLast(request)
        return true
    }

    @Synchronized
    fun drain(): List<InteractionActionRequest> {
        if (pendingRequests.isEmpty()) return emptyList()
        val drained = pendingRequests.toList()
        pendingRequests.clear()
        return drained
    }

    private companion object {
        private const val TAG = "InteractionActionRequestBus"
    }
}

class InteractionActionInterruptBus {
    private val pendingReasons = ArrayDeque<InteractionActionInterruptReason>()

    @Synchronized
    fun requestInterrupt(reason: InteractionActionInterruptReason) {
        pendingReasons.addLast(reason)
    }

    @Synchronized
    fun drain(): List<InteractionActionInterruptReason> {
        if (pendingReasons.isEmpty()) return emptyList()
        val drained = pendingReasons.toList()
        pendingReasons.clear()
        return drained
    }
}

class InteractionActionLockState {
    private data class ListenerSubscription(
        val listener: InteractionActionListener,
        val actionIds: Set<String>
    ) {
        fun accepts(actionId: String): Boolean {
            return actionIds.isEmpty() || actionIds.contains(actionId)
        }
    }

    private val subscriptions = mutableSetOf<ListenerSubscription>()

    var currentActionId: String? = null
        private set
    var currentControllerId: String? = null
        private set
    var currentSource: InteractionActionSource? = null
        private set

    val isLocked: Boolean
        get() = currentControllerId != null

    @Synchronized
    fun tryLock(request: InteractionActionRequest): Boolean {
        if (isLocked) return false
        currentActionId = request.actionId
        currentControllerId = request.controllerId
        currentSource = request.source
        subscriptions
            .filter { it.accepts(request.actionId) }
            .forEach { it.listener.onActionStarted(request.actionId, request.controllerId, request.source) }
        return true
    }

    @Synchronized
    fun canPreempt(request: InteractionActionRequest): Boolean {
        val source = currentSource ?: return false
        if (request.source.priority > source.priority) return true
        return request.source == InteractionActionSource.DEBUG &&
            source == InteractionActionSource.DEBUG &&
            request.controllerId != currentControllerId
    }

    @Synchronized
    fun release(controllerId: String, status: InteractionActionStatus) {
        if (currentControllerId != controllerId) return
        val actionId = currentActionId ?: return
        val source = currentSource ?: InteractionActionSource.DIALOGUE
        currentActionId = null
        currentControllerId = null
        currentSource = null
        subscriptions
            .filter { it.accepts(actionId) }
            .forEach { it.listener.onActionFinished(actionId, controllerId, source, status) }
    }

    @Synchronized
    fun addListener(listener: InteractionActionListener, actionIds: Set<String> = emptySet()) {
        subscriptions.removeAll { it.listener == listener }
        subscriptions.add(ListenerSubscription(listener, actionIds))
    }

    @Synchronized
    fun removeListener(listener: InteractionActionListener) {
        subscriptions.removeAll { it.listener == listener }
    }
}

private val InteractionActionSource.priority: Int
    get() = when (this) {
        InteractionActionSource.RANDOM -> 0
        InteractionActionSource.DIALOGUE -> 1
        InteractionActionSource.DEBUG -> 2
    }

object InteractionActionRuntimeDependencies {
    val actionRegistry = InteractionActionRegistry()
    val requestBus = InteractionActionRequestBus()
    val interruptBus = InteractionActionInterruptBus()
    val lockState = InteractionActionLockState()
    val entityIndex = InteractionEntityIndex()
}

class InteractionEntityIndex {
    private val actors = mutableMapOf<String, Entity>()
    private val objects = mutableMapOf<String, Entity>()

    @Synchronized
    fun clear() {
        actors.clear()
        objects.clear()
    }

    @Synchronized
    fun registerActor(entity: Entity) {
        val actorId = entity.components[InteractionActorComponent::class.java]?.actorId ?: return
        actors[actorId] = entity
    }

    @Synchronized
    fun registerObject(entity: Entity) {
        val objectId = entity.components[InteractionObjectComponent::class.java]?.objectId ?: return
        objects[objectId] = entity
    }

    @Synchronized
    fun findActor(actorId: String): Entity? {
        return actors[actorId]
    }

    @Synchronized
    fun findObject(objectId: String): Entity? {
        return objects[objectId]
    }
}

object InteractionEntityResolver {
    private val actorCondition =
        EntityQueryCondition.hasComponent(InteractionActorComponent::class.java)
    private val objectCondition =
        EntityQueryCondition.hasComponent(InteractionObjectComponent::class.java)

    fun findActor(scene: Scene, actorId: String): Entity? {
        InteractionActionRuntimeDependencies.entityIndex.findActor(actorId)?.let { return it }
        return scene.queryEntity(actorCondition).firstOrNull { entity ->
            entity.components[InteractionActorComponent::class.java]?.actorId == actorId
        }
    }

    fun findObject(scene: Scene, objectId: String): Entity? {
        InteractionActionRuntimeDependencies.entityIndex.findObject(objectId)?.let { return it }
        return scene.queryEntity(objectCondition).firstOrNull { entity ->
            entity.components[InteractionObjectComponent::class.java]?.objectId == objectId
        }
    }
}

const val DEFAULT_FAIRY_ACTOR_ID = "fairy"
