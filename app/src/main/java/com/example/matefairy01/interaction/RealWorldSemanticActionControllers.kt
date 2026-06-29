package com.example.matefairy01.interaction

import android.util.Log
import com.example.matefairy01.behavior.FairyBehaviorComponent
import com.example.matefairy01.behavior.FairySemanticResidenceComponent
import com.example.matefairy01.behavior.FairySemanticResidenceRuntime
import com.example.matefairy01.perception.RealWorldSemanticRuntimeDependencies
import com.example.matefairy01.perception.RealWorldSemanticTargetLocator
import com.example.matefairy01.perception.SemanticInteractionPurpose
import com.example.matefairy01.perception.SemanticSurfaceResolver
import com.example.matefairy01.perception.SemanticInteractionTarget
import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.math.EulerAngles
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.sense.base.SemanticLabelType
import kotlin.math.atan2
import kotlin.math.sqrt

class StayOnChairActionController : InteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(request: InteractionActionRequest): InteractionActionInstance {
        return StayOnSemanticObjectActionInstance(
            request = request,
            semantic = SemanticLabelType.CHAIR,
            purpose = SemanticInteractionPurpose.STAY_ON_CHAIR,
            residenceId = ACTION_ID
        )
    }

    companion object {
        const val ACTION_ID = "stay-on-chair"
    }
}

class LeaveChairActionController : InteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(request: InteractionActionRequest): InteractionActionInstance {
        return LeaveSemanticResidenceActionInstance(request)
    }

    companion object {
        const val ACTION_ID = "leave-chair"
    }
}

private class StayOnSemanticObjectActionInstance(
    private val request: InteractionActionRequest,
    private val semantic: SemanticLabelType,
    private val purpose: SemanticInteractionPurpose,
    private val residenceId: String
) : InteractionActionInstance {
    override val actionId: String = request.actionId

    private val motionTemplate = ActionSubjectMotionTemplate()
    private val locator = RealWorldSemanticTargetLocator(RealWorldSemanticRuntimeDependencies.query)
    private val surfaceResolver = SemanticSurfaceResolver()
    private val config = SemanticStayActionConfig.from(request.params)
    private var state = StayOnSemanticObjectState.RESOLVING_TARGET
    private var elapsedSeconds = 0f
    private var activeSubject: Entity? = null
    private var target: SemanticInteractionTarget? = null
    private var surfaceTargetPosition: Vector3? = null
    private var previousCollisionResponseMode: CollisionResponseMode? = null

    override fun update(context: SceneUpdateContext): InteractionActionStatus {
        if (request.source == InteractionActionSource.RANDOM) {
            return InteractionActionStatus.FAILED
        }
        elapsedSeconds += context.deltaTime

        val subject = resolveSubject(context)
            ?: return fail("missing-subject")
        val transform = subject.components[TransformComponent::class.java]
            ?: return fail("missing-transform")
        activeSubject = subject
        subject.components.set(FairyActionLockComponent(actionId))

        return when (state) {
            StayOnSemanticObjectState.RESOLVING_TARGET -> resolveTarget(transform)
            StayOnSemanticObjectState.RESOLVING_SURFACE -> resolveSurface(context)
            StayOnSemanticObjectState.MOVING_TO_TARGET -> moveToTarget(subject, transform, context.deltaTime)
            StayOnSemanticObjectState.SETTLING -> settleOnTarget(subject, transform)
        }
    }

    override fun cancel() {
        activeSubject?.let { subject ->
            motionTemplate.restore(subject)
            subject.components.remove(FairyActionLockComponent::class.java)
        }
        RealWorldSemanticRuntimeDependencies.query.finishSemanticScanRequest()
        activeSubject = null
    }

    private fun resolveSubject(context: SceneUpdateContext): Entity? {
        return activeSubject ?: InteractionEntityResolver.findActor(context.scene, request.subjectId)
            ?.also { activeSubject = it }
    }

    private fun resolveTarget(transform: TransformComponent): InteractionActionStatus {
        if (elapsedSeconds <= config.scanWarmupSeconds) {
            RealWorldSemanticRuntimeDependencies.query.requestSemanticScan(config.scanWindowMillis)
        }

        val resolved = locator.requestTarget(
            semantic = semantic,
            origin = transform.position,
            maxDistance = config.maxSearchDistance,
            purpose = purpose
        )
        if (resolved != null) {
            target = resolved
            state = StayOnSemanticObjectState.RESOLVING_SURFACE
            Log.d(TAG, "Resolved semantic target: semantic=$semantic, source=${resolved.source}")
            return InteractionActionStatus.RUNNING
        }

        return if (elapsedSeconds >= config.resolveTimeoutSeconds) {
            Log.w(TAG, "No semantic target found: semantic=$semantic")
            fail("target-timeout")
        } else {
            InteractionActionStatus.RUNNING
        }
    }

    private fun resolveSurface(context: SceneUpdateContext): InteractionActionStatus {
        val resolvedTarget = target ?: return fail("missing-target-before-surface")
        val placement = runCatching {
            surfaceResolver.resolvePlacement(
                scene = context.scene,
                target = resolvedTarget,
                surfaceOffset = config.surfaceOffset
            )
        }.onFailure { error ->
            Log.w(TAG, "Failed to resolve semantic surface; fallback to semantic target", error)
        }.getOrNull()

        surfaceTargetPosition = if (placement != null) {
            // Preserve semantic object's XZ so surface sampling can only correct height, not redirect movement.
            Vector3(
                resolvedTarget.position.x,
                placement.position.y,
                resolvedTarget.position.z
            )
        } else {
            resolvedTarget.position
        }
        state = StayOnSemanticObjectState.MOVING_TO_TARGET
        RealWorldSemanticRuntimeDependencies.query.finishSemanticScanRequest()
        Log.d(
            TAG,
            "Resolved semantic surface: semantic=$semantic, " +
                "semanticPosition=${resolvedTarget.position}, " +
                "surface=${placement?.source}, confidence=${placement?.confidence}, " +
                "surfacePosition=${placement?.position}, finalPosition=$surfaceTargetPosition"
        )
        return InteractionActionStatus.RUNNING
    }

    private fun moveToTarget(
        subject: Entity,
        transform: TransformComponent,
        dt: Float
    ): InteractionActionStatus {
        val targetPosition = surfaceTargetPosition ?: target?.position ?: return fail("missing-target-position")
        if (previousCollisionResponseMode == null) {
            previousCollisionResponseMode =
                subject.components[CollisionComponent::class.java]?.collisionResponseMode
        }
        motionTemplate.prepare(subject)
        val direction = direction(transform.position, targetPosition)
        val distanceToTarget = distance(transform.position, targetPosition)
        if (direction == null || distanceToTarget <= config.arrivalRadius) {
            state = StayOnSemanticObjectState.SETTLING
            return InteractionActionStatus.RUNNING
        }

        val step = (config.flySpeed * dt).coerceAtMost(distanceToTarget)
        transform.position = Vector3(
            transform.position.x + direction.x * step,
            transform.position.y + direction.y * step,
            transform.position.z + direction.z * step
        )
        faceDirection(transform, direction, dt, config.turnSpeed)
        syncBehaviorYaw(subject, transform)
        return InteractionActionStatus.RUNNING
    }

    private fun settleOnTarget(subject: Entity, transform: TransformComponent): InteractionActionStatus {
        val resolvedTarget = target ?: return fail("missing-target-settle")
        val finalPosition = surfaceTargetPosition ?: resolvedTarget.position
        motionTemplate.restore(subject)
        clearSubjectMotion(subject)
        transform.position = finalPosition
        syncBehaviorYaw(subject, transform)
        FairySemanticResidenceRuntime.enter(
            subject,
            FairySemanticResidenceComponent(
                residenceId = residenceId,
                semantic = semantic,
                anchorUUID = resolvedTarget.anchorUUID,
                targetPosition = finalPosition,
                targetRotation = resolvedTarget.rotation,
                previousCollisionResponseMode = previousCollisionResponseMode
            )
        )
        subject.components.remove(FairyActionLockComponent::class.java)
        return InteractionActionStatus.COMPLETED
    }

    private fun fail(reason: String): InteractionActionStatus {
        activeSubject?.let { subject ->
            motionTemplate.restore(subject)
            subject.components.remove(FairyActionLockComponent::class.java)
        }
        activeSubject = null
        RealWorldSemanticRuntimeDependencies.query.finishSemanticScanRequest()
        return InteractionActionStatus.FAILED
    }
}

private class LeaveSemanticResidenceActionInstance(
    private val request: InteractionActionRequest
) : InteractionActionInstance {
    override val actionId: String = request.actionId

    override fun update(context: SceneUpdateContext): InteractionActionStatus {
        val subject = InteractionEntityResolver.findActor(context.scene, request.subjectId)
            ?: return InteractionActionStatus.FAILED
        subject.components.set(FairyActionLockComponent(actionId))
        FairySemanticResidenceRuntime.clearIfEntity(subject)
        subject.components.remove(FairyActionLockComponent::class.java)
        return InteractionActionStatus.COMPLETED
    }
}

private enum class StayOnSemanticObjectState {
    RESOLVING_TARGET,
    RESOLVING_SURFACE,
    MOVING_TO_TARGET,
    SETTLING
}

private data class SemanticStayActionConfig(
    val maxSearchDistance: Float = 3.0f,
    val resolveTimeoutSeconds: Float = 2.5f,
    val scanWarmupSeconds: Float = 0.2f,
    val scanWindowMillis: Long = 2500L,
    val flySpeed: Float = 1.05f,
    val turnSpeed: Float = 7f,
    val arrivalRadius: Float = 0.12f,
    val surfaceOffset: Float = 0.0f
) {
    companion object {
        fun from(params: Map<String, Any>): SemanticStayActionConfig {
            return SemanticStayActionConfig(
                maxSearchDistance = (params["maxDistance"] as? Number)?.toFloat() ?: 3.0f,
                surfaceOffset = (params["surfaceOffset"] as? Number)?.toFloat() ?: 0.0f
            )
        }
    }
}

private fun clearSubjectMotion(subject: Entity) {
    subject.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
    subject.components[PhysicsVelocityComponent::class.java]?.let { velocity ->
        velocity.linearVelocity = Vector3.ZERO
        velocity.angularVelocity = Vector3.ZERO
    }
}

private fun syncBehaviorYaw(subject: Entity, transform: TransformComponent) {
    subject.components[FairyBehaviorComponent::class.java]?.currentYaw = transform.eulerAngles.yaw
}

private fun faceDirection(
    transform: TransformComponent,
    direction: Vector3,
    dt: Float,
    turnSpeed: Float
) {
    if (direction.x == 0f && direction.z == 0f) return
    val targetYaw = atan2(direction.x, direction.z).toDegrees()
    val current = transform.eulerAngles
    val t = (turnSpeed * dt).coerceIn(0f, 1f)
    transform.eulerAngles = EulerAngles(
        pitch = current.pitch,
        yaw = lerpAngle(current.yaw, targetYaw, t),
        roll = current.roll
    )
}

private fun direction(from: Vector3, to: Vector3): Vector3? {
    val x = to.x - from.x
    val y = to.y - from.y
    val z = to.z - from.z
    val length = sqrt(x * x + y * y + z * z)
    if (length <= 0.001f) return null
    return Vector3(x / length, y / length, z / length)
}

private fun distance(a: Vector3, b: Vector3): Float {
    val x = a.x - b.x
    val y = a.y - b.y
    val z = a.z - b.z
    return sqrt(x * x + y * y + z * z)
}

private fun lerpAngle(a: Float, b: Float, t: Float): Float {
    var diff = b - a
    while (diff < -180f) diff += 360f
    while (diff > 180f) diff -= 360f
    return a + diff * t
}

private fun Float.toDegrees() = this * 180f / kotlin.math.PI.toFloat()

private const val TAG = "RealWorldSemanticAction"
