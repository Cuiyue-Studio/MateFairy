package com.example.matefairy01.interaction

import android.util.Log
import com.example.matefairy01.behavior.FairySemanticResidenceRuntime
import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.EntityQueryCondition
import com.pico.spatial.core.ecs.Scene
import com.pico.spatial.core.ecs.SceneUpdateContext
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

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
        // #region debug-point B:interaction-controller-register
        debugChairChain("B", "InteractionActionCore.kt:InteractionActionRegistry.register", "Interaction controller register", "actionId" to controller.actionId, "controller" to controller::class.java.simpleName)
        // #endregion
    }

    fun get(actionId: String): InteractionActionController? {
        val controller = controllers[actionId]
        // #region debug-point B:interaction-controller-get
        debugChairChain("B", "InteractionActionCore.kt:InteractionActionRegistry.get", "Interaction controller get", "actionId" to actionId, "controller" to (controller?.javaClass?.simpleName ?: "null"), "registered" to controllers.keys.sorted().joinToString("|"))
        // #endregion
        return controller
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
            // #region debug-point B:request-bus-reject-blank
            debugChairChain("B", "InteractionActionCore.kt:enqueue", "RequestBus reject blank", "actionId" to request.actionId)
            // #endregion
            return false
        }
        if (request.source == InteractionActionSource.RANDOM && FairySemanticResidenceRuntime.isActive()) {
            Log.d(TAG, "Ignore random action=${request.actionId}; fairy is in semantic residence")
            // #region debug-point B:request-bus-reject-random-residence
            debugChairChain("B", "InteractionActionCore.kt:enqueue", "RequestBus reject random residence", "actionId" to request.actionId, "source" to request.source)
            // #endregion
            return false
        }
        val lockState = InteractionActionRuntimeDependencies.lockState
        if (lockState.isLocked && !lockState.canPreempt(request)) {
            Log.d(TAG, "Ignore action=${request.actionId}; action lock is active")
            // #region debug-point B:request-bus-reject-lock
            debugChairChain("B", "InteractionActionCore.kt:enqueue", "RequestBus reject lock", "actionId" to request.actionId, "source" to request.source, "currentAction" to lockState.currentActionId, "currentController" to lockState.currentControllerId)
            // #endregion
            return false
        }
        pendingRequests.addLast(request)
        // #region debug-point B:request-bus-enqueue
        debugChairChain("B", "InteractionActionCore.kt:enqueue", "RequestBus enqueue", "actionId" to request.actionId, "controllerId" to request.controllerId, "source" to request.source, "subjectId" to request.subjectId, "queueSize" to pendingRequests.size)
        // #endregion
        return true
    }

    @Synchronized
    fun drain(): List<InteractionActionRequest> {
        if (pendingRequests.isEmpty()) return emptyList()
        val drained = pendingRequests.toList()
        pendingRequests.clear()
        // #region debug-point B:request-bus-drain
        debugChairChain("B", "InteractionActionCore.kt:drain", "RequestBus drain", "count" to drained.size, "actions" to drained.joinToString("|") { it.actionId + ":" + it.source.name })
        // #endregion
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

// #region debug-point B:interaction-core-reporter
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
