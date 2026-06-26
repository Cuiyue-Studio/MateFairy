package com.example.matefairy01.interaction

import android.util.Log
import com.example.matefairy01.behavior.FairySemanticResidenceRuntime
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.System
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * 持续调度实体交互 action。不同 controllerId 可并行运行，便于后续扩展多个动作控制器。
 */
class InteractionActionSystem : System() {
    private val activeActions = mutableMapOf<String, InteractionActionInstance>()

    override fun update(context: SceneUpdateContext) {
        val interruptReasons = InteractionActionRuntimeDependencies.interruptBus.drain()
        if (interruptReasons.isNotEmpty()) {
            interruptActiveActions(interruptReasons)
            return
        }

        InteractionActionRuntimeDependencies.requestBus.drain().forEach { request ->
            // #region debug-point B:action-system-drained-request
            debugChairChain("B", "InteractionActionSystem.kt:update", "ActionSystem received request", "actionId" to request.actionId, "controllerId" to request.controllerId, "source" to request.source)
            // #endregion
            if (request.source == InteractionActionSource.RANDOM && FairySemanticResidenceRuntime.isActive()) {
                Log.d(TAG, "Ignore random action=${request.actionId}; fairy is in semantic residence")
                // #region debug-point B:action-system-reject-random-residence
                debugChairChain("B", "InteractionActionSystem.kt:update", "ActionSystem reject random residence", "actionId" to request.actionId)
                // #endregion
                return@forEach
            }
            if (InteractionActionRuntimeDependencies.lockState.canPreempt(request)) {
                val preemptedControllerId = InteractionActionRuntimeDependencies.lockState.currentControllerId
                val preemptedActionId = InteractionActionRuntimeDependencies.lockState.currentActionId
                if (preemptedControllerId != null) {
                    activeActions.remove(preemptedControllerId)?.cancel()
                    InteractionActionRuntimeDependencies.lockState.release(
                        preemptedControllerId,
                        InteractionActionStatus.FAILED
                    )
                }
            }
            if (!InteractionActionRuntimeDependencies.lockState.tryLock(request)) {
                Log.d(TAG, "Ignore action=${request.actionId}; another action is running")
                // #region debug-point B:action-system-lock-failed
                debugChairChain("B", "InteractionActionSystem.kt:update", "ActionSystem lock failed", "actionId" to request.actionId, "currentAction" to InteractionActionRuntimeDependencies.lockState.currentActionId, "currentController" to InteractionActionRuntimeDependencies.lockState.currentControllerId)
                // #endregion
                return@forEach
            }

            val controller = InteractionActionRuntimeDependencies.actionRegistry.get(request.actionId)
            if (controller == null) {
                Log.w(TAG, "No interaction controller for action=${request.actionId}")
                // #region debug-point B:action-system-no-controller
                debugChairChain("B", "InteractionActionSystem.kt:update", "ActionSystem no controller", "actionId" to request.actionId)
                // #endregion
                InteractionActionRuntimeDependencies.lockState.release(
                    request.controllerId,
                    InteractionActionStatus.FAILED
                )
                return@forEach
            }

            activeActions.remove(request.controllerId)?.cancel()
            activeActions[request.controllerId] = controller.createInstance(request)
            // #region debug-point B:action-system-instance-created
            debugChairChain("B", "InteractionActionSystem.kt:update", "ActionSystem instance created", "actionId" to request.actionId, "controllerId" to request.controllerId, "controller" to controller::class.java.simpleName)
            // #endregion
        }

        val iterator = activeActions.iterator()
        while (iterator.hasNext()) {
            val (controllerId, instance) = iterator.next()
            when (instance.update(context)) {
                InteractionActionStatus.RUNNING -> {
                    // #region debug-point B:action-system-running
                    if (instance.actionId == "stay-on-chair") debugChairChain("B", "InteractionActionSystem.kt:update", "ActionSystem action running", "actionId" to instance.actionId, "controllerId" to controllerId)
                    // #endregion
                }
                InteractionActionStatus.COMPLETED -> {
                    // #region debug-point E:action-system-completed
                    debugChairChain("E", "InteractionActionSystem.kt:update", "ActionSystem action completed", "actionId" to instance.actionId, "controllerId" to controllerId)
                    // #endregion
                    iterator.remove()
                    InteractionActionRuntimeDependencies.lockState.release(
                        controllerId,
                        InteractionActionStatus.COMPLETED
                    )
                }
                InteractionActionStatus.FAILED -> {
                    Log.w(TAG, "Interaction action failed: controllerId=$controllerId")
                    // #region debug-point B:action-system-failed
                    debugChairChain("B", "InteractionActionSystem.kt:update", "ActionSystem action failed", "actionId" to instance.actionId, "controllerId" to controllerId)
                    // #endregion
                    iterator.remove()
                    InteractionActionRuntimeDependencies.lockState.release(
                        controllerId,
                        InteractionActionStatus.FAILED
                    )
                }
            }
        }
    }

    private fun interruptActiveActions(reasons: List<InteractionActionInterruptReason>) {
        if (activeActions.isEmpty() && !InteractionActionRuntimeDependencies.lockState.isLocked) return
        val reasonText = reasons.joinToString(separator = ",") { it.name }
        val actions = activeActions.toMap()
        activeActions.clear()
        actions.forEach { (controllerId, instance) ->
            instance.cancel()
            InteractionActionRuntimeDependencies.lockState.release(
                controllerId,
                InteractionActionStatus.FAILED
            )
            Log.d(TAG, "Interrupted action=${instance.actionId}, reason=$reasonText")
        }
    }

    private companion object {
        private const val TAG = "InteractionActionSystem"
    }
}

// #region debug-point B:action-system-reporter
private fun debugChairChain(hypothesisId: String, location: String, msg: String, vararg fields: Pair<String, Any?>) {
    thread(start = true) {
        runCatching {
            val data = fields.joinToString(",") { "\"${it.first}\":\"${it.second.toString().replace("\\", "\\\\").replace("\"", "\\\"")}\"" }
            val body = "{\"sessionId\":\"chair-command-chain\",\"runId\":\"pre-fix\",\"hypothesisId\":\"$hypothesisId\",\"location\":\"$location\",\"msg\":\"[DEBUG] $msg\",\"data\":{$data},\"ts\":${java.lang.System.currentTimeMillis()}}"
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
