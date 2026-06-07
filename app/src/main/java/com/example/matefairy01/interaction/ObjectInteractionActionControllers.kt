package com.example.matefairy01.interaction

import android.util.Log
import com.example.matefairy01.audio.MusicModule
import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.ecs.simulation.RigidBodyMode
import com.pico.spatial.core.math.EulerAngles
import com.pico.spatial.core.math.Vector3
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class StartBoomboxActionController(
    private val musicModule: MusicModule
) : InteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(request: InteractionActionRequest): InteractionActionInstance {
        return CarryAndUseObjectActionInstance(
            request = request,
            defaultObjectId = DEFAULT_OBJECT_ID,
            config = CarryActionConfig(
                approachDistance = 0.35f,
                holdOffset = Vector3(0f, -0.03f, 0.16f),
                useIntervalSeconds = 0.2f,
                useCount = 1,
                finishAfterUse = true
            ),
            onUse = { boombox ->
                playObjectAnimation(boombox)
                musicModule.playNextSpatialMusicAt(boombox)
            }
        )
    }

    companion object {
        const val ACTION_ID = "start-boombox"
        const val DEFAULT_OBJECT_ID = "boombox"
    }
}

class StopBoomboxActionController(
    private val musicModule: MusicModule
) : InteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(request: InteractionActionRequest): InteractionActionInstance {
        return PutDownObjectActionInstance(
            request = request,
            defaultObjectId = StartBoomboxActionController.DEFAULT_OBJECT_ID,
            onBeforePutDown = { boombox ->
                playObjectAnimation(boombox)
                musicModule.stopSpatialMusic()
            }
        )
    }

    companion object {
        const val ACTION_ID = "stop-boombox"
    }
}

class SqueezeRubberDuckActionController(
    private val musicModule: MusicModule
) : InteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(request: InteractionActionRequest): InteractionActionInstance {
        return CarryAndUseObjectActionInstance(
            request = request,
            defaultObjectId = DEFAULT_OBJECT_ID,
            config = CarryActionConfig(
                approachDistance = 0.26f,
                holdOffset = Vector3(0f, -0.02f, 0.24f),
                useIntervalSeconds = 2f,
                useCount = 3,
                finishAfterUse = true
            ),
            onUse = { duck ->
                playObjectAnimation(duck)
                musicModule.playRandomRubberDuckSfxAt(duck)
            }
        )
    }

    companion object {
        const val ACTION_ID = "squeeze-rubber-duck"
        const val DEFAULT_OBJECT_ID = "rubber_duck_toy"
    }
}

class PutDownRubberDuckActionController : InteractionActionController {
    override val actionId: String = ACTION_ID

    override fun createInstance(request: InteractionActionRequest): InteractionActionInstance {
        return PutDownObjectActionInstance(
            request = request,
            defaultObjectId = SqueezeRubberDuckActionController.DEFAULT_OBJECT_ID
        )
    }

    companion object {
        const val ACTION_ID = "put-down-rubber-duck"
    }
}

private class CarryAndUseObjectActionInstance(
    private val request: InteractionActionRequest,
    private val defaultObjectId: String,
    private val config: CarryActionConfig,
    private val onUse: (Entity) -> Unit
) : InteractionActionInstance {
    override val actionId: String = request.actionId

    private val objectId = request.objectIds.firstOrNull() ?: defaultObjectId
    private var state = CarryUseState.APPROACHING
    private var useTimerSeconds = 0f
    private var usedCount = 0
    private var finishTimerSeconds = 0f
    private var debugElapsedSeconds = 0f
    private var debugLastState: CarryUseState? = null
    private var debugLastSampleSeconds = -1f
    private var activeSubject: Entity? = null
    private var previousSubjectRigidBodyMode: RigidBodyMode? = null

    override fun update(context: SceneUpdateContext): InteractionActionStatus {
        debugElapsedSeconds += context.deltaTime
        val subject = InteractionEntityResolver.findActor(context.scene, request.subjectId)
        if (subject == null) {
            return InteractionActionStatus.FAILED
        }
        val target = InteractionEntityResolver.findObject(context.scene, objectId)
        if (target == null) {
            return InteractionActionStatus.FAILED
        }
        val subjectTransform = subject.components[TransformComponent::class.java]
        if (subjectTransform == null) {
            return InteractionActionStatus.FAILED
        }
        val targetTransform = target.components[TransformComponent::class.java]
        if (targetTransform == null) {
            return InteractionActionStatus.FAILED
        }

        subject.components.set(FairyActionLockComponent(actionId))
        activeSubject = subject
        prepareSubjectForDirectMotion(subject)
        if (debugLastState != state) {
            debugLastState = state
        }

        return when (state) {
            CarryUseState.APPROACHING -> {
                val directionToTarget = horizontalDirection(subjectTransform.position, targetTransform.position)
                    ?: forwardFromYaw(subjectTransform.eulerAngles.yaw)
                val approachTarget = Vector3(
                    targetTransform.position.x - directionToTarget.x * config.approachDistance,
                    targetTransform.position.y + config.hoverHeight,
                    targetTransform.position.z - directionToTarget.z * config.approachDistance
                )
                moveSubjectTowards(subject, subjectTransform, approachTarget, context.deltaTime, config)
                faceDirection(subjectTransform, directionToTarget, context.deltaTime, config)
                if (debugElapsedSeconds - debugLastSampleSeconds >= 1f) {
                    debugLastSampleSeconds = debugElapsedSeconds
                    val force = subject.components[PhysicsForceComponent::class.java]?.force
                }
                if (distance(subjectTransform.position, approachTarget) <= config.arrivalRadius) {
                    state = CarryUseState.PICKING_UP
                }
                InteractionActionStatus.RUNNING
            }

            CarryUseState.PICKING_UP -> {
                clearSubjectForce(subject)
                val follow = PickedObjectFollowComponent(
                    holderActorId = request.subjectId,
                    localOffset = config.holdOffset
                )
                target.components[CollisionComponent::class.java]?.let { collision ->
                    follow.previousCollisionResponseMode = collision.collisionResponseMode
                    collision.collisionResponseMode = CollisionResponseMode.TRIGGER_LITE
                }
                target.components.set(follow)
                target.components[RigidBodyComponent::class.java]?.let { rigidBody ->
                    rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
                    rigidBody.isAffectedByGravity = false
                }
                target.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
                target.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO
                state = CarryUseState.USING
                InteractionActionStatus.RUNNING
            }

            CarryUseState.USING -> {
                useTimerSeconds += context.deltaTime
                if (usedCount == 0 || useTimerSeconds >= config.useIntervalSeconds) {
                    useTimerSeconds = 0f
                    usedCount += 1
                    onUse(target)
                }
                if (config.finishAfterUse && usedCount >= config.useCount) {
                    state = CarryUseState.FINISHING
                }
                InteractionActionStatus.RUNNING
            }

            CarryUseState.FINISHING -> {
                finishTimerSeconds += context.deltaTime
                if (finishTimerSeconds >= config.finishDelaySeconds) {
                    restoreSubjectMotion(subject)
                    subject.components.remove(FairyActionLockComponent::class.java)
                    InteractionActionStatus.COMPLETED
                } else {
                    InteractionActionStatus.RUNNING
                }
            }
        }
    }

    override fun cancel() {
        activeSubject?.let(::restoreSubjectMotion)
        Log.d(TAG, "Cancel carry/use action: $actionId")
    }

    private fun prepareSubjectForDirectMotion(subject: Entity) {
        val rigidBody = subject.components[RigidBodyComponent::class.java] ?: return
        if (previousSubjectRigidBodyMode == null) {
            previousSubjectRigidBodyMode = rigidBody.rigidBodyMode
        }
        rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
        rigidBody.isAffectedByGravity = false
        subject.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
        subject.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO
    }

    private fun restoreSubjectMotion(subject: Entity) {
        subject.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
        subject.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO
        subject.components[RigidBodyComponent::class.java]?.let { rigidBody ->
            rigidBody.rigidBodyMode = previousSubjectRigidBodyMode ?: RigidBodyMode.DYNAMIC
            rigidBody.isAffectedByGravity = false
        }
        previousSubjectRigidBodyMode = null
    }
}

private class PutDownObjectActionInstance(
    private val request: InteractionActionRequest,
    private val defaultObjectId: String,
    private val onBeforePutDown: (Entity) -> Unit = {}
) : InteractionActionInstance {
    override val actionId: String = request.actionId

    private val objectId = request.objectIds.firstOrNull() ?: defaultObjectId
    private var hasPutDown = false
    private var finishTimerSeconds = 0f

    override fun update(context: SceneUpdateContext): InteractionActionStatus {
        val subject = InteractionEntityResolver.findActor(context.scene, request.subjectId)
            ?: return InteractionActionStatus.FAILED
        val target = InteractionEntityResolver.findObject(context.scene, objectId)
            ?: return InteractionActionStatus.FAILED
        val subjectTransform = subject.components[TransformComponent::class.java]
            ?: return InteractionActionStatus.FAILED
        val targetTransform = target.components[TransformComponent::class.java]
            ?: return InteractionActionStatus.FAILED

        subject.components.set(FairyActionLockComponent(actionId))

        if (!hasPutDown) {
            clearSubjectForce(subject)
            onBeforePutDown(target)
            val follow = target.components[PickedObjectFollowComponent::class.java]
            target.components.remove(PickedObjectFollowComponent::class.java)
            target.components[CollisionComponent::class.java]?.collisionResponseMode =
                follow?.previousCollisionResponseMode ?: CollisionResponseMode.COLLIDER_FULL
            val forward = forwardFromYaw(subjectTransform.eulerAngles.yaw)
            targetTransform.position = Vector3(
                subjectTransform.position.x + forward.x * 0.42f,
                subjectTransform.position.y - 0.28f,
                subjectTransform.position.z + forward.z * 0.42f
            )
            target.components[RigidBodyComponent::class.java]?.let { rigidBody ->
                rigidBody.rigidBodyMode = RigidBodyMode.DYNAMIC
                rigidBody.isAffectedByGravity = true
            }
            target.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
            target.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO
            hasPutDown = true
        }

        finishTimerSeconds += context.deltaTime
        return if (finishTimerSeconds >= 0.35f) {
            subject.components.remove(FairyActionLockComponent::class.java)
            InteractionActionStatus.COMPLETED
        } else {
            InteractionActionStatus.RUNNING
        }
    }
}

private enum class CarryUseState {
    APPROACHING,
    PICKING_UP,
    USING,
    FINISHING
}

private data class CarryActionConfig(
    val approachDistance: Float,
    val holdOffset: Vector3,
    val hoverHeight: Float = 0.25f,
    val arrivalRadius: Float = 0.2f,
    val flySpeed: Float = 1.15f,
    val steeringGain: Float = 22f,
    val maxSteeringForce: Float = 52f,
    val turnSpeed: Float = 8f,
    val useIntervalSeconds: Float,
    val useCount: Int,
    val finishAfterUse: Boolean,
    val finishDelaySeconds: Float = 0.35f
)

private fun moveSubjectTowards(
    subject: Entity,
    transform: TransformComponent,
    target: Vector3,
    dt: Float,
    config: CarryActionConfig
) {
    val force = subject.components[PhysicsForceComponent::class.java]
        ?: PhysicsForceComponent().also { subject.components.set(it) }
    val direction = direction(transform.position, target)
    if (direction == null || dt <= 0f) {
        force.force = Vector3.ZERO
        return
    }
    val distanceToTarget = distance(transform.position, target)
    val step = (config.flySpeed * dt).coerceAtMost(distanceToTarget)
    transform.position = Vector3(
        transform.position.x + direction.x * step,
        transform.position.y + direction.y * step,
        transform.position.z + direction.z * step
    )
    force.force = Vector3.ZERO
}

private fun faceDirection(transform: TransformComponent, direction: Vector3, dt: Float, config: CarryActionConfig) {
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

private fun playObjectAnimation(entity: Entity, trackIndex: Int = 0) {
    runCatching {
        val resources = entity.getAnimationResources()
        if (resources.isNotEmpty() && trackIndex in resources.indices) {
            entity.playAnimation(resources[trackIndex])
        }
    }.onFailure {
        Log.w(TAG, "Object animation is not ready for entity=${entity.getName()}", it)
    }
}

private fun clearSubjectForce(subject: Entity) {
    subject.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
}

private fun horizontalDirection(from: Vector3, to: Vector3): Vector3? {
    val x = to.x - from.x
    val z = to.z - from.z
    val length = sqrt(x * x + z * z)
    if (length <= 0.001f) return null
    return Vector3(x / length, 0f, z / length)
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

private const val TAG = "ObjectInteractionAction"
