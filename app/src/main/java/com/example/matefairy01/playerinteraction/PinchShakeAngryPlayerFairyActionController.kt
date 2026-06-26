package com.example.matefairy01.playerinteraction

import android.util.Log
import com.example.matefairy01.animation.FairyAnimation
import com.example.matefairy01.behavior.FairyBehaviorComponent
import com.example.matefairy01.interaction.ActionSubjectMotionTemplate
import com.example.matefairy01.interaction.InteractionEntityResolver
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.math.EulerAngles
import com.pico.spatial.core.math.Vector3
import kotlin.math.PI
import kotlin.math.sin

class PinchShakeAngryPlayerFairyActionController(
    private val animationScheduler: PlayerFairyAnimationScheduler
) : PlayerFairyInteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(
        request: PlayerFairyInteractionRequest
    ): PlayerFairyInteractionActionInstance {
        return PinchShakeAngryPlayerFairyActionInstance(request, animationScheduler)
    }

    companion object {
        const val ACTION_ID = "player-pinch-shake-angry"
    }
}

private class PinchShakeAngryPlayerFairyActionInstance(
    private val request: PlayerFairyInteractionRequest,
    private val animationScheduler: PlayerFairyAnimationScheduler
) : PlayerFairyInteractionActionInstance {
    override val actionId: String = request.actionId

    private val subjectMotion = ActionSubjectMotionTemplate()
    private var activeSubject: Entity? = null
    private var activeVisual: Entity? = null
    private var baseVisualPosition: Vector3? = null
    private var baseVisualEuler: EulerAngles? = null
    private var elapsedSeconds = 0f
    private var angryAnimationStarted = false

    override fun update(context: SceneUpdateContext): PlayerFairyInteractionActionStatus {
        elapsedSeconds += context.deltaTime
        val subject = InteractionEntityResolver.findActor(context.scene, request.subjectId)
            ?: return PlayerFairyInteractionActionStatus.FAILED
        val subjectTransform = subject.components[TransformComponent::class.java]
            ?: return PlayerFairyInteractionActionStatus.FAILED
        val behavior = subject.components[FairyBehaviorComponent::class.java]
        val visual = behavior?.visualEntity ?: subject
        val visualTransform = visual.components[TransformComponent::class.java]
            ?: return PlayerFairyInteractionActionStatus.FAILED

        if (activeSubject == null) {
            activeSubject = subject
            activeVisual = visual
            baseVisualPosition = visualTransform.position
            baseVisualEuler = visualTransform.eulerAngles
            subject.components.set(PlayerFairyInteractionComponent(actionId, request.trigger))
            subjectMotion.prepare(subject)
            Log.d(TAG, "Start player pinch interaction: trigger=${request.trigger}")
        }

        subject.components.set(PlayerFairyInteractionComponent(actionId, request.trigger))
        applyShake(visualTransform)

        if (!angryAnimationStarted && elapsedSeconds >= SHAKE_DURATION_SECONDS) {
            restoreVisualTransform()
            angryAnimationStarted = animationScheduler.playPlayerFairyInteractionAnimation(
                ownerInteractionId = request.controllerId,
                animation = FairyAnimation.MAD_ACTION
            )
            if (!angryAnimationStarted) {
                return PlayerFairyInteractionActionStatus.FAILED
            }
        }

        return if (elapsedSeconds >= TOTAL_DURATION_SECONDS) {
            finish()
            PlayerFairyInteractionActionStatus.COMPLETED
        } else {
            PlayerFairyInteractionActionStatus.RUNNING
        }
    }

    override fun cancel() {
        finish()
        Log.d(TAG, "Cancel player pinch interaction")
    }

    private fun applyShake(visualTransform: TransformComponent) {
        if (elapsedSeconds > SHAKE_DURATION_SECONDS) return
        val basePosition = baseVisualPosition ?: visualTransform.position
        val baseEuler = baseVisualEuler ?: visualTransform.eulerAngles
        val normalized = (elapsedSeconds / SHAKE_DURATION_SECONDS).coerceIn(0f, 1f)
        val damping = 1f - normalized
        val wave = sin(normalized * SHAKE_OSCILLATION_COUNT * 2f * PI.toFloat())
        val offsetX = SHAKE_POSITION_AMPLITUDE_METERS * wave * damping
        val roll = baseEuler.roll + SHAKE_ROLL_AMPLITUDE_DEGREES * wave * damping
        visualTransform.position = Vector3(
            basePosition.x + offsetX,
            basePosition.y,
            basePosition.z
        )
        visualTransform.eulerAngles = EulerAngles(
            pitch = baseEuler.pitch,
            yaw = baseEuler.yaw,
            roll = roll
        )
    }

    private fun finish() {
        restoreVisualTransform()
        animationScheduler.stopPlayerFairyInteractionAnimation(request.controllerId)
        activeSubject?.let { subject ->
            subjectMotion.restore(subject)
            subject.components.remove(PlayerFairyInteractionComponent::class.java)
        }
        activeSubject = null
        activeVisual = null
    }

    private fun restoreVisualTransform() {
        val visualTransform = activeVisual?.components?.get(TransformComponent::class.java) ?: return
        baseVisualPosition?.let { visualTransform.position = it }
        baseVisualEuler?.let { visualTransform.eulerAngles = it }
    }

    private companion object {
        private const val TAG = "PinchShakeAngryAction"
        private const val SHAKE_DURATION_SECONDS = 0.45f
        private const val SHAKE_OSCILLATION_COUNT = 2.5f
        private const val SHAKE_POSITION_AMPLITUDE_METERS = 0.035f
        private const val SHAKE_ROLL_AMPLITUDE_DEGREES = 10f
        private val TOTAL_DURATION_SECONDS: Float =
            SHAKE_DURATION_SECONDS + (FairyAnimation.MAD_ACTION.durationMs.toFloat() / 1000f)
    }
}
