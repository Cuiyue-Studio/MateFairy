package com.example.matefairy01.interaction

import android.util.Log
import com.example.matefairy01.animation.ActionAnimationScheduler
import com.example.matefairy01.animation.AnimationConfig
import com.example.matefairy01.content.ResourcePhysicsActivationComponent
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.math.EulerAngles
import com.pico.spatial.core.math.Vector3
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class PlayFootballActionController(
    private val actionAnimationScheduler: ActionAnimationScheduler
) : InteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(request: InteractionActionRequest): InteractionActionInstance {
        return PlayFootballActionInstance(
            request = request,
            actionAnimationScheduler = actionAnimationScheduler
        )
    }

    companion object {
        const val ACTION_ID = "play-football"
    }
}

private class PlayFootballActionInstance(
    private val request: InteractionActionRequest,
    private val actionAnimationScheduler: ActionAnimationScheduler
) : InteractionActionInstance {
    override val actionId: String = PlayFootballActionController.ACTION_ID

    private val footballObjectId = request.objectIds.firstOrNull() ?: DEFAULT_FOOTBALL_OBJECT_ID
    private val config = PlayFootballConfig.from(request.params)
    private var state = PlayFootballState.DETECTING
    private var finishTimerSeconds = 0f
    private var elapsedSeconds = 0f
    private var activeSubject: com.pico.spatial.core.ecs.Entity? = null
    private var activeFootball: com.pico.spatial.core.ecs.Entity? = null
    private val subjectMotion = ActionSubjectMotionTemplate()
    private var kickAnimationStarted = false
    private var postKickRetreatTarget: Vector3? = null

    override fun update(context: SceneUpdateContext): InteractionActionStatus {
        elapsedSeconds += context.deltaTime
        val subject = resolveSubject(context)
            ?: return InteractionActionStatus.FAILED
        val football = resolveFootball(context)
            ?: return InteractionActionStatus.FAILED

        val subjectTransform = subject.components[TransformComponent::class.java]
            ?: return InteractionActionStatus.FAILED
        val footballTransform = football.components[TransformComponent::class.java]
            ?: return InteractionActionStatus.FAILED

        if (elapsedSeconds >= config.maxActionSeconds) {
            cleanupSubjectMotion(subject)
            return InteractionActionStatus.FAILED
        }

        if (state == PlayFootballState.DETECTING) {
            val activation = football.components[ResourcePhysicsActivationComponent::class.java]
            if (activation != null && !activation.activated) {
                cleanupSubjectMotion(subject)
                return InteractionActionStatus.FAILED
            }
            if (footballTransform.position.y < MIN_TARGET_Y) {
                cleanupSubjectMotion(subject)
                return InteractionActionStatus.FAILED
            }
            if (horizontalDistance(subjectTransform.position, footballTransform.position) >
                config.activationHorizontalDistance
            ) {
                cleanupSubjectMotion(subject)
                return InteractionActionStatus.FAILED
            }
            state = PlayFootballState.APPROACHING
        }

        subject.components.set(FairyActionLockComponent(actionId))
        activeSubject = subject
        prepareSubjectForDirectMotion(subject)

        return when (state) {
            PlayFootballState.DETECTING -> InteractionActionStatus.RUNNING

            PlayFootballState.APPROACHING -> {
                val subjectPosition = subjectTransform.position
                val footballPosition = footballTransform.position
                val directionToFootball = horizontalDirection(subjectPosition, footballPosition)
                    ?: forwardFromYaw(subjectTransform.eulerAngles.yaw)
                val approachDistance = config.approachDistance.coerceAtLeast(MIN_SAFE_APPROACH_DISTANCE)
                val approachTarget = Vector3(
                    footballPosition.x - directionToFootball.x * approachDistance,
                    footballPosition.y + config.hoverHeightAboveBall,
                    footballPosition.z - directionToFootball.z * approachDistance
                )

                moveSubjectTowards(subject, subjectTransform, approachTarget, context.deltaTime)
                faceDirection(subjectTransform, directionToFootball, context.deltaTime)

                if (distance(subjectTransform.position, approachTarget) <= config.arrivalRadius) {
                    clearSubjectForce(subject)
                    state = PlayFootballState.PLAYING_KICK_ANIMATION
                }
                InteractionActionStatus.RUNNING
            }

            PlayFootballState.PLAYING_KICK_ANIMATION -> {
                val subjectForward = horizontalDirection(subjectTransform.position, footballTransform.position)
                    ?: forwardFromYaw(subjectTransform.eulerAngles.yaw)
                faceDirection(subjectTransform, subjectForward, context.deltaTime)
                clearSubjectForce(subject)
                if (!kickAnimationStarted) {
                    kickAnimationStarted = true
                    val scheduled = actionAnimationScheduler.playActionAnimation(
                        ownerActionId = actionId,
                        animation = AnimationConfig.playFootballKickAnimation
                    )
                    if (!scheduled) {
                        state = PlayFootballState.KICKING
                    }
                } else if (!actionAnimationScheduler.isActionAnimationPlaying(
                        ownerActionId = actionId,
                        animation = AnimationConfig.playFootballKickAnimation
                    )
                ) {
                    state = PlayFootballState.KICKING
                }
                InteractionActionStatus.RUNNING
            }

            PlayFootballState.KICKING -> {
                val subjectForward = horizontalDirection(subjectTransform.position, footballTransform.position)
                    ?: forwardFromYaw(subjectTransform.eulerAngles.yaw)
                faceDirection(subjectTransform, subjectForward, context.deltaTime)
                kickFootball(football, subjectForward)
                clearSubjectForce(subject)
                postKickRetreatTarget = buildPostKickRetreatTarget(footballTransform.position, subjectForward)
                state = PlayFootballState.FINISHING
                InteractionActionStatus.RUNNING
            }

            PlayFootballState.FINISHING -> {
                finishTimerSeconds += context.deltaTime
                val retreatTarget = postKickRetreatTarget
                if (retreatTarget != null) {
                    val retreatDirection = horizontalDirection(subjectTransform.position, retreatTarget)
                    if (retreatDirection != null) {
                        faceDirection(subjectTransform, retreatDirection, context.deltaTime)
                    }
                    moveSubjectTowards(subject, subjectTransform, retreatTarget, context.deltaTime)
                    if (distance(subjectTransform.position, retreatTarget) <= config.arrivalRadius) {
                        postKickRetreatTarget = null
                    }
                }
                if (
                    finishTimerSeconds >= config.finishDelaySeconds &&
                    postKickRetreatTarget == null
                ) {
                    cleanupSubjectMotion(subject)
                    InteractionActionStatus.COMPLETED
                } else {
                    InteractionActionStatus.RUNNING
                }
            }
        }
    }

    override fun cancel() {
        activeSubject?.let(::cleanupSubjectMotion)
        Log.d(TAG, "Cancel play-football action")
    }

    private fun resolveSubject(context: SceneUpdateContext): com.pico.spatial.core.ecs.Entity? {
        return activeSubject ?: InteractionEntityResolver.findActor(context.scene, request.subjectId)
            ?.also { activeSubject = it }
    }

    private fun resolveFootball(context: SceneUpdateContext): com.pico.spatial.core.ecs.Entity? {
        return activeFootball ?: InteractionEntityResolver.findObject(context.scene, footballObjectId)
            ?.also { activeFootball = it }
    }

    private fun moveSubjectTowards(
        subject: com.pico.spatial.core.ecs.Entity,
        transform: TransformComponent,
        target: Vector3,
        dt: Float
    ) {
        val force = subject.components[PhysicsForceComponent::class.java]
            ?: PhysicsForceComponent().also { subject.components.set(it) }
        val currentPosition = transform.position
        val direction = direction(currentPosition, target)
        if (direction == null || dt <= 0f) {
            force.force = Vector3.ZERO
            return
        }

        val distanceToTarget = distance(currentPosition, target)
        val step = (config.flySpeed * dt).coerceAtMost(distanceToTarget)
        transform.position = Vector3(
            currentPosition.x + direction.x * step,
            currentPosition.y + direction.y * step,
            currentPosition.z + direction.z * step
        )
        force.force = Vector3.ZERO
    }

    private fun faceDirection(transform: TransformComponent, direction: Vector3, dt: Float) {
        if (direction.x == 0f && direction.z == 0f) return
        val targetYaw = atan2(direction.x, direction.z).toDegrees()
        val current = transform.eulerAngles
        val t = (config.turnSpeed * dt).coerceIn(0f, 1f)
        transform.eulerAngles = EulerAngles(
            pitch = current.pitch,
            yaw = lerpAngle(current.yaw, targetYaw, t),
            roll = current.roll
        )
    }

    private fun kickFootball(football: com.pico.spatial.core.ecs.Entity, subjectForward: Vector3) {
        val yawOffsetDegrees = config.kickYawOffsetDegrees.coerceIn(
            -config.maxKickFanAngleDegrees,
            config.maxKickFanAngleDegrees
        )
        val kickDirection = rotateHorizontal(subjectForward, yawOffsetDegrees)
        val kickSpeed = config.kickSpeed.coerceIn(config.minKickSpeed, config.maxKickSpeed)

        football.components[RigidBodyComponent::class.java]?.isAffectedByGravity = true
        val velocity = football.components[PhysicsVelocityComponent::class.java]
            ?: PhysicsVelocityComponent().also { football.components.set(it) }
        velocity.linearVelocity = Vector3(
            kickDirection.x * kickSpeed,
            config.upwardSpeed,
            kickDirection.z * kickSpeed
        )
    }

    private fun clearSubjectForce(subject: com.pico.spatial.core.ecs.Entity) {
        subject.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
    }

    private fun buildPostKickRetreatTarget(footballPosition: Vector3, kickDirection: Vector3): Vector3 {
        return Vector3(
            footballPosition.x - kickDirection.x * config.postKickRestoreDistance,
            footballPosition.y + config.hoverHeightAboveBall,
            footballPosition.z - kickDirection.z * config.postKickRestoreDistance
        )
    }

    private fun prepareSubjectForDirectMotion(subject: com.pico.spatial.core.ecs.Entity) {
        subjectMotion.prepare(subject)
    }

    private fun cleanupSubjectMotion(subject: com.pico.spatial.core.ecs.Entity) {
        subject.components.remove(FairyActionLockComponent::class.java)
        subjectMotion.restore(subject)
        postKickRetreatTarget = null
        activeSubject = null
        activeFootball = null
    }

    private companion object {
        private const val TAG = "PlayFootballAction"
        private const val DEFAULT_FOOTBALL_OBJECT_ID = "football"
        private const val MIN_TARGET_Y = -0.5f
        private const val MIN_SAFE_APPROACH_DISTANCE = 0.22f
    }
}

private enum class PlayFootballState {
    DETECTING,
    APPROACHING,
    PLAYING_KICK_ANIMATION,
    KICKING,
    FINISHING
}

private data class PlayFootballConfig(
    val approachDistance: Float = 0.22f,
    val arrivalRadius: Float = 0.18f,
    val hoverHeightAboveBall: Float = 0.28f,
    val flySpeed: Float = 1.2f,
    val steeringGain: Float = 22f,
    val maxSteeringForce: Float = 55f,
    val turnSpeed: Float = 8f,
    val minKickSpeed: Float = 0.8f,
    val maxKickSpeed: Float = 3.2f,
    val kickSpeed: Float = 1.8f,
    val upwardSpeed: Float = 0.75f,
    val maxKickFanAngleDegrees: Float = 25f,
    val kickYawOffsetDegrees: Float = 0f,
    val finishDelaySeconds: Float = 0.35f,
    val postKickRestoreDistance: Float = 0.52f,
    val maxActionSeconds: Float = 6.5f,
    val activationHorizontalDistance: Float = PlayFootballPreconditions.MAX_HORIZONTAL_DISTANCE_METERS
) {
    companion object {
        fun from(params: Map<String, Any>): PlayFootballConfig {
            return PlayFootballConfig(
                kickSpeed = params.floatParam("kickSpeed", 1.8f),
                upwardSpeed = params.floatParam("upwardSpeed", 0.75f),
                kickYawOffsetDegrees = params.floatParam("kickYawOffsetDegrees", 0f),
                maxKickFanAngleDegrees = params.floatParam("maxKickFanAngleDegrees", 25f),
                activationHorizontalDistance = params.floatParam(
                    "activationHorizontalDistance",
                    PlayFootballPreconditions.MAX_HORIZONTAL_DISTANCE_METERS
                )
            )
        }
    }
}

private fun Map<String, Any>.floatParam(key: String, default: Float): Float {
    return when (val value = this[key]) {
        is Float -> value
        is Double -> value.toFloat()
        is Int -> value.toFloat()
        is Long -> value.toFloat()
        is String -> value.toFloatOrNull() ?: default
        else -> default
    }
}

private fun horizontalDirection(from: Vector3, to: Vector3): Vector3? {
    val x = to.x - from.x
    val z = to.z - from.z
    val length = sqrt(x * x + z * z)
    if (length <= 0.001f) return null
    return Vector3(x / length, 0f, z / length)
}

private fun horizontalDistance(from: Vector3, to: Vector3): Float {
    val x = to.x - from.x
    val z = to.z - from.z
    return sqrt(x * x + z * z)
}

private fun direction(from: Vector3, to: Vector3): Vector3? {
    val x = to.x - from.x
    val y = to.y - from.y
    val z = to.z - from.z
    val length = sqrt(x * x + y * y + z * z)
    if (length <= 0.001f) return null
    return Vector3(x / length, y / length, z / length)
}

private fun forwardFromYaw(yawDegrees: Float): Vector3 {
    val radians = yawDegrees.toRadians()
    return Vector3(sin(radians), 0f, cos(radians))
}

private fun rotateHorizontal(direction: Vector3, degrees: Float): Vector3 {
    val radians = degrees.toRadians()
    val cosValue = cos(radians)
    val sinValue = sin(radians)
    return Vector3(
        direction.x * cosValue - direction.z * sinValue,
        0f,
        direction.x * sinValue + direction.z * cosValue
    )
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

private fun Float.toRadians() = this * PI.toFloat() / 180f

private fun Float.toDegrees() = this * 180f / PI.toFloat()
