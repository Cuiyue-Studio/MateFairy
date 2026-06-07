package com.example.matefairy01.interaction

import android.util.Log
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

class PlayFootballActionController : InteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(request: InteractionActionRequest): InteractionActionInstance {
        return PlayFootballActionInstance(request)
    }

    companion object {
        const val ACTION_ID = "play-football"
    }
}

private class PlayFootballActionInstance(
    private val request: InteractionActionRequest
) : InteractionActionInstance {
    override val actionId: String = PlayFootballActionController.ACTION_ID

    private val footballObjectId = request.objectIds.firstOrNull() ?: DEFAULT_FOOTBALL_OBJECT_ID
    private val config = PlayFootballConfig.from(request.params)
    private var state = PlayFootballState.DETECTING
    private var finishTimerSeconds = 0f

    override fun update(context: SceneUpdateContext): InteractionActionStatus {
        val subject = InteractionEntityResolver.findActor(context.scene, request.subjectId)
            ?: return InteractionActionStatus.FAILED
        val football = InteractionEntityResolver.findObject(context.scene, footballObjectId)
            ?: return InteractionActionStatus.FAILED

        val subjectTransform = subject.components[TransformComponent::class.java]
            ?: return InteractionActionStatus.FAILED
        val footballTransform = football.components[TransformComponent::class.java]
            ?: return InteractionActionStatus.FAILED

        subject.components.set(FairyActionLockComponent(actionId))

        return when (state) {
            PlayFootballState.DETECTING -> {
                if (horizontalDistance(subjectTransform.position, footballTransform.position) >
                    config.activationHorizontalDistance
                ) {
                    return InteractionActionStatus.FAILED
                }
                state = PlayFootballState.APPROACHING
                InteractionActionStatus.RUNNING
            }

            PlayFootballState.APPROACHING -> {
                val subjectPosition = subjectTransform.position
                val footballPosition = footballTransform.position
                val directionToFootball = horizontalDirection(subjectPosition, footballPosition)
                    ?: forwardFromYaw(subjectTransform.eulerAngles.yaw)
                val approachTarget = Vector3(
                    footballPosition.x - directionToFootball.x * config.approachDistance,
                    footballPosition.y + config.hoverHeightAboveBall,
                    footballPosition.z - directionToFootball.z * config.approachDistance
                )

                moveSubjectTowards(subject, subjectTransform, approachTarget, context.deltaTime)
                faceDirection(subjectTransform, directionToFootball, context.deltaTime)

                if (distance(subjectPosition, approachTarget) <= config.arrivalRadius) {
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

                // TODO: 接入 Action 类动画调度器后，在这里传入 KICK_FOOTBALL 枚举并触发踢球动画。
                state = PlayFootballState.FINISHING
                InteractionActionStatus.RUNNING
            }

            PlayFootballState.FINISHING -> {
                finishTimerSeconds += context.deltaTime
                if (finishTimerSeconds >= config.finishDelaySeconds) {
                    subject.components.remove(FairyActionLockComponent::class.java)
                    InteractionActionStatus.COMPLETED
                } else {
                    InteractionActionStatus.RUNNING
                }
            }
        }
    }

    override fun cancel() {
        Log.d(TAG, "Cancel play-football action")
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

        force.force = Vector3(
            (direction.x * config.flySpeed * config.steeringGain).coerceIn(
                -config.maxSteeringForce,
                config.maxSteeringForce
            ),
            (direction.y * config.flySpeed * config.steeringGain).coerceIn(
                -config.maxSteeringForce,
                config.maxSteeringForce
            ),
            (direction.z * config.flySpeed * config.steeringGain).coerceIn(
                -config.maxSteeringForce,
                config.maxSteeringForce
            )
        )
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

    private companion object {
        private const val TAG = "PlayFootballAction"
        private const val DEFAULT_FOOTBALL_OBJECT_ID = "football"
    }
}

private enum class PlayFootballState {
    DETECTING,
    APPROACHING,
    KICKING,
    FINISHING
}

private data class PlayFootballConfig(
    val approachDistance: Float = 0.32f,
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
