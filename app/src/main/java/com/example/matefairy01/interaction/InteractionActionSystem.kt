package com.example.matefairy01.interaction

import android.util.Log
import com.example.matefairy01.behavior.FairySemanticResidenceRuntime
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.System

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
            if (request.source == InteractionActionSource.RANDOM && FairySemanticResidenceRuntime.isActive()) {
                Log.d(TAG, "Ignore random action=${request.actionId}; fairy is in semantic residence")
                return@forEach
            }
            if (InteractionActionRuntimeDependencies.lockState.canPreempt(request)) {
                val preemptedControllerId = InteractionActionRuntimeDependencies.lockState.currentControllerId
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
                return@forEach
            }

            val controller = InteractionActionRuntimeDependencies.actionRegistry.get(request.actionId)
            if (controller == null) {
                Log.w(TAG, "No interaction controller for action=${request.actionId}")
                InteractionActionRuntimeDependencies.lockState.release(
                    request.controllerId,
                    InteractionActionStatus.FAILED
                )
                return@forEach
            }

            activeActions.remove(request.controllerId)?.cancel()
            activeActions[request.controllerId] = controller.createInstance(request)
        }

        val iterator = activeActions.iterator()
        while (iterator.hasNext()) {
            val (controllerId, instance) = iterator.next()
            when (instance.update(context)) {
                InteractionActionStatus.RUNNING -> Unit
                InteractionActionStatus.COMPLETED -> {
                    iterator.remove()
                    InteractionActionRuntimeDependencies.lockState.release(
                        controllerId,
                        InteractionActionStatus.COMPLETED
                    )
                }
                InteractionActionStatus.FAILED -> {
                    Log.w(TAG, "Interaction action failed: controllerId=$controllerId")
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
