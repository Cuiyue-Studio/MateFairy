package com.example.matefairy01.interaction

import android.util.Log
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.System

/**
 * 持续调度实体交互 action。不同 controllerId 可并行运行，便于后续扩展多个动作控制器。
 */
class InteractionActionSystem : System() {
    private val activeActions = mutableMapOf<String, InteractionActionInstance>()

    override fun update(context: SceneUpdateContext) {
        InteractionActionRuntimeDependencies.requestBus.drain().forEach { request ->
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

    private companion object {
        private const val TAG = "InteractionActionSystem"
    }
}
