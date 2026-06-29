package com.example.matefairy01.behavior

import com.example.matefairy01.animation.AnimationFacingPolicy
import com.example.matefairy01.avatar.AvatarController
import com.example.matefairy01.content.ResourcePhysicsActivationComponent
import com.pico.spatial.core.ecs.System
import com.pico.spatial.core.ecs.Scene
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.EntityQueryCondition
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.resource.ShapeResource
import com.pico.spatial.core.ecs.simulation.CollisionCastHitMode
import com.pico.spatial.core.ecs.simulation.CollisionGroup
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.ecs.simulation.RigidBodyMode
import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.core.math.EulerAngles
import com.example.matefairy01.interaction.ActionRecoveryGraceComponent
import com.example.matefairy01.interaction.FairyActionLockComponent
import com.example.matefairy01.interaction.InteractionActorComponent
import com.example.matefairy01.interaction.InteractionActionRequest
import com.example.matefairy01.interaction.InteractionActionRuntimeDependencies
import com.example.matefairy01.interaction.InteractionActionSource
import com.example.matefairy01.interaction.InteractionEntityResolver
import com.example.matefairy01.interaction.PickedObjectFollowComponent
import com.example.matefairy01.interaction.PlayFootballActionController
import com.example.matefairy01.interaction.PlayFootballPreconditions
import com.example.matefairy01.perception.SpatialMeshRuntimeDependencies
import com.example.matefairy01.playerinteraction.PlayerFairyInteractionComponent
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.abs
import kotlin.random.Random

class FairyBehaviorSystem : System() {
    private val hmdCondition = EntityQueryCondition.hasComponent(HMDTagComponent::class.java)
    private val fairyCondition =
        EntityQueryCondition.hasComponent(FairyBehaviorComponent::class.java)
    private val pickedCondition =
        EntityQueryCondition.hasComponent(PickedObjectFollowComponent::class.java)

    private var cachedHmdEntity: Entity? = null
    private var cachedFairyEntities: List<Entity> = emptyList()
    private var wasFollowEnabled: Boolean = true
    private var spatialMeshCastShape: ShapeResource? = null

    override fun update(context: SceneUpdateContext) {
        val dt = context.deltaTime
        if (dt <= 0f) return

        val hmdEntity = resolveHmdEntity(context) ?: return
        val hmdTransform = hmdEntity.components[TransformComponent::class.java] ?: return
        val hmdPos = hmdTransform.position
        val avatarController = BehaviorRuntimeDependencies.avatarController

        val fairyEntities = resolveFairyEntities(context)
        if (fairyEntities.isEmpty()) return
        val followEnabled = FairyFollowControlModule.isFollowEnabled
        val followJustRestored = !wasFollowEnabled && followEnabled

        for (fairyEntity in fairyEntities) {
            val behavior = fairyEntity.components[FairyBehaviorComponent::class.java]!!
            val transform = fairyEntity.components[TransformComponent::class.java] ?: continue
            val visualTransform =
                behavior.visualEntity?.components?.get(TransformComponent::class.java) ?: transform

            val animationFacingPolicy = avatarController.facingPolicy()

            if (handleActionRecoveryGrace(fairyEntity, behavior, transform, visualTransform, hmdPos, avatarController, dt)) {
                continue
            }

            if (
                fairyEntity.components[PlayerFairyInteractionComponent::class.java] != null ||
                fairyEntity.components[FairyActionLockComponent::class.java] != null ||
                !followEnabled
            ) {
                prepareScriptDrivenFairy(fairyEntity)
                visualTransform.position = transform.position
                if (animationFacingPolicy == AnimationFacingPolicy.FACE_PLAYER) {
                    faceTargetPosition(
                        behavior = behavior,
                        from = transform.position,
                        target = hmdPos,
                        turnSpeed = ANIMATION_FACE_PLAYER_TURN_SPEED,
                        dt = dt
                    )
                    applyFairyYaw(transform, visualTransform, behavior)
                } else {
                    behavior.currentYaw = transform.eulerAngles.yaw
                    visualTransform.eulerAngles = transform.eulerAngles
                }
                behavior.lastPosition = transform.position
                behavior.hasRecordedLastPosition = true
                continue
            }

            val semanticResidence = fairyEntity.components[FairySemanticResidenceComponent::class.java]
            if (semanticResidence != null) {
                applySemanticResidenceIdle(
                    fairyEntity = fairyEntity,
                    residence = semanticResidence,
                    behavior = behavior,
                    transform = transform,
                    visualTransform = visualTransform,
                    hmdPos = hmdPos,
                    animationFacingPolicy = animationFacingPolicy,
                    avatarController = avatarController,
                    dt = dt
                )
                continue
            }

            val fairyPos = transform.position
            updatePlayFootballDistance(context, fairyPos)
            if (followJustRestored) {
                if (isCarryingObject(context, fairyEntity)) {
                    resumeCarryingObjectAfterAction(
                        fairyEntity = fairyEntity,
                        behavior = behavior,
                        transform = transform,
                        visualTransform = visualTransform,
                        hmdEntity = hmdEntity,
                        hmdPos = hmdPos,
                        avatarController = avatarController
                    )
                    continue
                }
                resetBehaviorStateAfterAction(
                    fairyEntity = fairyEntity,
                    behavior = behavior,
                    transform = transform,
                    visualTransform = visualTransform,
                    avatarController = avatarController
                )
                continue
            }

            // Calculate 3D distance
            val dx = fairyPos.x - hmdPos.x
            val dy = fairyPos.y - hmdPos.y
            val dz = fairyPos.z - hmdPos.z
            val distance2D = sqrt(dx * dx + dz * dz)

            // Initialize last position if not done
            if (!behavior.hasRecordedLastPosition) {
                behavior.lastPosition = fairyPos
                behavior.hasRecordedLastPosition = true
            }

            // State machine update
            when (behavior.state) {
                FairyState.FOLLOW_HOVERING -> {
                    if (behavior.waitTimer <= 0f) {
                        behavior.waitTimer = Random.nextFloat() * 1f + 1f // Hover 1-2 seconds
                    }
                    behavior.waitTimer -= dt
                    if (behavior.waitTimer <= 0f) {
                        behavior.state = FairyState.RANDOM_MOVING
                        behavior.waitTimer = 0f
                        behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
                        avatarController?.requestMovingAnimation()
                    }
                }

                FairyState.RANDOM_MOVING -> {
                    if (distance2D > behavior.outerRadius) {
                        behavior.state = FairyState.FOLLOWING
                        behavior.waitTimer = 0f
                        behavior.currentTarget = getFollowViewCenterTarget(hmdEntity, fairyEntity, behavior)
                        avatarController?.requestMovingAnimation()
                    } else {
                        if (behavior.currentTarget == null || hasReachedTarget(fairyPos, behavior.currentTarget!!)) {
                            behavior.state = FairyState.RANDOM_WAITING
                            scheduleRandomRestBehavior(context, behavior, avatarController, fairyPos)
                            behavior.baseY = transform.position.y
                        }
                    }
                }

                FairyState.RANDOM_WAITING -> {
                    if (!behavior.isWaitingForAnimation && behavior.waitTimer <= 0f) {
                        behavior.waitTimer = Random.nextFloat() * 2f + 1f
                    }

                    behavior.waitTimer -= dt

                    if (behavior.waitTimer <= 0f) {
                        behavior.state = FairyState.RANDOM_MOVING
                        behavior.waitTimer = 0f
                        behavior.isWaitingForAnimation = false
                        behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
                        avatarController?.requestMovingAnimation()
                    }

                    if (distance2D > behavior.outerRadius) {
                        behavior.state = FairyState.FOLLOWING
                        behavior.waitTimer = 0f
                        behavior.isWaitingForAnimation = false
                        behavior.currentTarget = getFollowViewCenterTarget(hmdEntity, fairyEntity, behavior)
                        avatarController?.requestMovingAnimation()
                    }
                }

                FairyState.FOLLOWING -> {
                    val followTarget = getFollowViewCenterTarget(hmdEntity, fairyEntity, behavior)
                    behavior.currentTarget = followTarget

                    if (hasReachedTarget(fairyPos, followTarget)) {
                        behavior.state = FairyState.FOLLOW_HOVERING
                        behavior.waitTimer = 0f
                        behavior.baseY = transform.position.y

                        scheduleRandomRestBehavior(context, behavior, avatarController, fairyPos)
                    }
                }
            }

            val isCarryingObject = isCarryingObject(context, fairyEntity)
            if (isCarryingObject) {
                ensureCarryingMovementTarget(
                    behavior = behavior,
                    fairyEntity = fairyEntity,
                    hmdEntity = hmdEntity,
                    transform = transform,
                    hmdPos = hmdPos
                )
                applyCarryingDirectMotion(
                    scene = context.scene,
                    fairyEntity = fairyEntity,
                    behavior = behavior,
                    transform = transform,
                    hmdPos = hmdPos,
                    animationFacingPolicy = animationFacingPolicy,
                    dt = dt
                )
                visualTransform.position = transform.position
                applyFairyYaw(transform, visualTransform, behavior)
                behavior.lastPosition = transform.position
                behavior.hasRecordedLastPosition = true
                continue
            }

            applyDirectBehaviorMotion(
                scene = context.scene,
                fairyEntity = fairyEntity,
                behavior = behavior,
                transform = transform,
                visualTransform = visualTransform,
                hmdPos = hmdPos,
                animationFacingPolicy = animationFacingPolicy,
                dt = dt
            )
        }
        wasFollowEnabled = followEnabled
    }

    private fun isCarryingObject(context: SceneUpdateContext, fairyEntity: Entity): Boolean {
        val actorId = fairyEntity.components[InteractionActorComponent::class.java]?.actorId
            ?: DEFAULT_BEHAVIOR_FAIRY_ACTOR_ID
        return context.scene.queryEntity(pickedCondition).any { picked ->
            picked.components[PickedObjectFollowComponent::class.java]?.holderActorId == actorId
        }
    }

    private fun handleActionRecoveryGrace(
        fairyEntity: Entity,
        behavior: FairyBehaviorComponent,
        transform: TransformComponent,
        visualTransform: TransformComponent,
        hmdPos: Vector3,
        avatarController: AvatarController?,
        dt: Float
    ): Boolean {
        val recovery = fairyEntity.components[ActionRecoveryGraceComponent::class.java] ?: return false
        val anchorPosition = recovery.anchorPosition ?: transform.position.also {
            recovery.anchorPosition = it
        }

        recovery.remainingSeconds -= dt

        if (!recovery.hasResetBehaviorState) {
            resetBehaviorStateAfterAction(
                fairyEntity = fairyEntity,
                behavior = behavior,
                transform = transform,
                visualTransform = visualTransform,
                avatarController = avatarController
            )
            recovery.hasResetBehaviorState = true
        }

        prepareScriptDrivenFairy(fairyEntity)
        transform.position = anchorPosition

        behavior.lastPosition = anchorPosition
        behavior.hasRecordedLastPosition = true
        behavior.baseY = anchorPosition.y
        behavior.currentTarget = null
        behavior.isWaitingForAnimation = false
        behavior.motionSpeed = 0f
        behavior.inertiaVelocity = Vector3.ZERO
        behavior.isInertiaSliding = false

        visualTransform.position = anchorPosition
        if (avatarController.facingPolicy() == AnimationFacingPolicy.FACE_PLAYER) {
            faceTargetPosition(
                behavior = behavior,
                from = anchorPosition,
                target = hmdPos,
                turnSpeed = ANIMATION_FACE_PLAYER_TURN_SPEED,
                dt = dt
            )
        }
        applyFairyYaw(transform, visualTransform, behavior)

        if (recovery.remainingSeconds <= 0f) {
            prepareScriptDrivenFairy(fairyEntity)
            transform.position = anchorPosition
            visualTransform.position = anchorPosition
            behavior.lastPosition = anchorPosition
            behavior.hasRecordedLastPosition = true
            behavior.baseY = anchorPosition.y
            behavior.state = FairyState.RANDOM_WAITING
            behavior.currentTarget = null
            behavior.waitTimer = POST_ACTION_IDLE_SECONDS
            behavior.isWaitingForAnimation = false
            behavior.velocity = Vector3.ZERO
            behavior.motionSpeed = 0f
            behavior.inertiaVelocity = Vector3.ZERO
            behavior.isInertiaSliding = false
            fairyEntity.components.remove(ActionRecoveryGraceComponent::class.java)
        }
        return true
    }

    private fun applyDirectBehaviorMotion(
        scene: Scene,
        fairyEntity: Entity,
        behavior: FairyBehaviorComponent,
        transform: TransformComponent,
        visualTransform: TransformComponent,
        hmdPos: Vector3,
        animationFacingPolicy: AnimationFacingPolicy,
        dt: Float
    ) {
        prepareScriptDrivenFairy(fairyEntity)

        val current = transform.position
        var next = current
        var travelDirection: Vector3? = null

        when (behavior.state) {
            FairyState.RANDOM_MOVING, FairyState.FOLLOWING -> {
                val target = behavior.currentTarget
                val direction = target?.let { direction(current, it) }
                if (target != null && direction != null) {
                    val distanceToTarget = distance(current, target)
                    val baseSpeed = if (behavior.state == FairyState.FOLLOWING) {
                        behavior.followSpeed * FOLLOWING_SPEED_SCALE
                    } else {
                        behavior.speed
                    }
                    val easedSpeed = updateEasedMotionSpeed(
                        behavior = behavior,
                        targetSpeed = baseSpeed,
                        distanceToTarget = distanceToTarget,
                        dt = dt
                    )
                    val step = (easedSpeed * dt).coerceAtMost(distanceToTarget)
                    val desiredNext = Vector3(
                        current.x + direction.x * step,
                        current.y + direction.y * step,
                        current.z + direction.z * step
                    )
                    next = constrainBySpatialMesh(
                        scene = scene,
                        fairyEntity = fairyEntity,
                        current = current,
                        desiredNext = desiredNext
                    )
                    travelDirection = direction
                }
            }

            FairyState.RANDOM_WAITING, FairyState.FOLLOW_HOVERING -> {
                behavior.motionSpeed = 0f
                behavior.floatTime += dt
                val floatOffset = behavior.floatAmplitude * sin(behavior.floatTime * behavior.floatSpeed) *
                    abs(sin(behavior.floatTime * behavior.floatSpeed * 0.7f))
                val targetY = behavior.baseY + floatOffset
                val maxYStep = DIRECT_IDLE_VERTICAL_SPEED * dt
                val yDelta = (targetY - current.y).coerceIn(-maxYStep, maxYStep)
                next = Vector3(current.x, current.y + yDelta, current.z)

                if (behavior.state == FairyState.FOLLOW_HOVERING) {
                    val faceDirX = hmdPos.x - current.x
                    val faceDirZ = hmdPos.z - current.z
                    if (faceDirX != 0f || faceDirZ != 0f) {
                        val targetYaw = atan2(faceDirX, faceDirZ).toDegrees()
                        behavior.currentYaw = lerpAngle(behavior.currentYaw, targetYaw, 3.0f * dt)
                    }
                }
            }
        }

        if (animationFacingPolicy == AnimationFacingPolicy.FACE_PLAYER) {
            faceTargetPosition(
                behavior = behavior,
                from = next,
                target = hmdPos,
                turnSpeed = ANIMATION_FACE_PLAYER_TURN_SPEED,
                dt = dt
            )
        } else if (behavior.state == FairyState.FOLLOWING) {
            faceTargetPosition(
                behavior = behavior,
                from = next,
                target = hmdPos,
                turnSpeed = FOLLOWING_LOOK_TURN_SPEED,
                dt = dt
            )
        } else {
            travelDirection?.let { direction ->
                faceDirection(
                    behavior = behavior,
                    direction = direction,
                    turnSpeed = MOVEMENT_TURN_SPEED,
                    dt = dt
                )
            }
        }

        transform.position = next
        visualTransform.position = next
        applyFairyYaw(transform, visualTransform, behavior)

        val safeDt = dt.coerceAtLeast(0.001f)
        behavior.velocity = Vector3(
            (next.x - current.x) / safeDt,
            (next.y - current.y) / safeDt,
            (next.z - current.z) / safeDt
        )
        behavior.lastPosition = next
        behavior.hasRecordedLastPosition = true
        behavior.baseY = if (behavior.state == FairyState.RANDOM_WAITING || behavior.state == FairyState.FOLLOW_HOVERING) {
            behavior.baseY
        } else {
            next.y
        }
    }

    private fun applyCarryingDirectMotion(
        scene: Scene,
        fairyEntity: Entity,
        behavior: FairyBehaviorComponent,
        transform: TransformComponent,
        hmdPos: Vector3,
        animationFacingPolicy: AnimationFacingPolicy,
        dt: Float
    ) {
        prepareScriptDrivenFairy(fairyEntity)
        val target = behavior.currentTarget ?: return
        val direction = direction(transform.position, target) ?: return
        val speed = when (behavior.state) {
            FairyState.FOLLOWING -> behavior.followSpeed
            FairyState.RANDOM_MOVING -> behavior.speed
            FairyState.RANDOM_WAITING,
            FairyState.FOLLOW_HOVERING -> 0.35f
        }.let { baseSpeed ->
            val cappedSpeed = if (behavior.state == FairyState.FOLLOWING) {
                baseSpeed * FOLLOWING_SPEED_SCALE
            } else {
                baseSpeed
            }.coerceAtMost(MAX_CARRYING_DIRECT_SPEED)
            updateEasedMotionSpeed(
                behavior = behavior,
                targetSpeed = cappedSpeed,
                distanceToTarget = distance(transform.position, target),
                dt = dt
            )
        }
        val distanceToTarget = distance(transform.position, target)
        val current = transform.position
        val step = (speed * dt).coerceAtMost(distanceToTarget)
        val desiredNext = Vector3(
            current.x + direction.x * step,
            current.y + direction.y * step,
            current.z + direction.z * step
        )
        transform.position = constrainBySpatialMesh(
            scene = scene,
            fairyEntity = fairyEntity,
            current = current,
            desiredNext = desiredNext
        )
        val safeDt = dt.coerceAtLeast(0.001f)
        behavior.velocity = Vector3(
            (transform.position.x - current.x) / safeDt,
            (transform.position.y - current.y) / safeDt,
            (transform.position.z - current.z) / safeDt
        )
        if (animationFacingPolicy == AnimationFacingPolicy.FACE_PLAYER) {
            faceTargetPosition(
                behavior = behavior,
                from = transform.position,
                target = hmdPos,
                turnSpeed = ANIMATION_FACE_PLAYER_TURN_SPEED,
                dt = dt
            )
        } else if (behavior.state == FairyState.FOLLOWING) {
            faceTargetPosition(
                behavior = behavior,
                from = transform.position,
                target = hmdPos,
                turnSpeed = FOLLOWING_LOOK_TURN_SPEED,
                dt = dt
            )
        } else {
            faceDirection(
                behavior = behavior,
                direction = direction,
                turnSpeed = MOVEMENT_TURN_SPEED,
                dt = dt
            )
        }
    }

    private fun applySemanticResidenceIdle(
        fairyEntity: Entity,
        residence: FairySemanticResidenceComponent,
        behavior: FairyBehaviorComponent,
        transform: TransformComponent,
        visualTransform: TransformComponent,
        hmdPos: Vector3,
        animationFacingPolicy: AnimationFacingPolicy,
        avatarController: AvatarController?,
        dt: Float
    ) {
        prepareScriptDrivenFairy(fairyEntity)

        transform.position = residence.targetPosition
        visualTransform.position = residence.targetPosition
        if (animationFacingPolicy == AnimationFacingPolicy.FACE_PLAYER) {
            faceTargetPosition(
                behavior = behavior,
                from = residence.targetPosition,
                target = hmdPos,
                turnSpeed = ANIMATION_FACE_PLAYER_TURN_SPEED,
                dt = dt
            )
            applyFairyYaw(transform, visualTransform, behavior)
        } else {
            behavior.currentYaw = transform.eulerAngles.yaw
            visualTransform.eulerAngles = transform.eulerAngles
        }
        behavior.lastPosition = residence.targetPosition
        behavior.hasRecordedLastPosition = true
        behavior.currentTarget = null
        behavior.waitTimer = 0f
        behavior.isWaitingForAnimation = false
        behavior.state = FairyState.RANDOM_WAITING
        behavior.motionSpeed = 0f
        behavior.inertiaVelocity = Vector3.ZERO
        behavior.isInertiaSliding = false

        residence.idleRefreshSeconds -= dt
        if (residence.idleRefreshSeconds <= 0f) {
            avatarController?.requestStandbyAnimation()
            residence.idleRefreshSeconds = RESIDENCE_IDLE_REFRESH_SECONDS
        }
    }

    private fun resumeCarryingObjectAfterAction(
        fairyEntity: Entity,
        behavior: FairyBehaviorComponent,
        transform: TransformComponent,
        visualTransform: TransformComponent,
        hmdEntity: Entity,
        hmdPos: Vector3,
        avatarController: AvatarController?
    ) {
        val fairyPos = transform.position
        prepareScriptDrivenFairy(fairyEntity)
        behavior.lastPosition = fairyPos
        behavior.hasRecordedLastPosition = true
        behavior.baseY = fairyPos.y
        behavior.waitTimer = 0f
        behavior.isWaitingForAnimation = false
        behavior.velocity = Vector3.ZERO
        behavior.motionSpeed = 0f
        behavior.inertiaVelocity = Vector3.ZERO
        behavior.isInertiaSliding = false
        behavior.state = if (horizontalDistance(fairyPos, hmdPos) > behavior.outerRadius) {
            FairyState.FOLLOWING
        } else {
            FairyState.RANDOM_MOVING
        }
        behavior.currentTarget = if (behavior.state == FairyState.FOLLOWING) {
            getFollowViewCenterTarget(hmdEntity, fairyEntity, behavior)
        } else {
            getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
        }
        visualTransform.position = fairyPos
        applyFairyYaw(transform, visualTransform, behavior)
        avatarController?.requestMovingAnimation()
    }

    private fun getFollowViewCenterTarget(
        hmdEntity: Entity,
        fairyEntity: Entity,
        behavior: FairyBehaviorComponent
    ): Vector3 {
        val hmdTransform = hmdEntity.components[TransformComponent::class.java]
        val hmdOrigin = hmdTransform?.position ?: return behavior.currentTarget ?: Vector3.ZERO
        val forward = hmdTransform?.eulerAngles?.yaw
            ?.let(::viewForwardFromYaw)
            ?: DEFAULT_HMD_FORWARD
        return Vector3(
            hmdOrigin.x + forward.x * FOLLOW_VIEW_CENTER_DISTANCE,
            hmdOrigin.y + behavior.hoverHeight,
            hmdOrigin.z + forward.z * FOLLOW_VIEW_CENTER_DISTANCE
        )
    }

    private fun getRandomTargetInInnerRadius(hmdPos: Vector3, innerRadius: Float, hoverHeight: Float, zDeviationRange: Float): Vector3 {
        val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
        val r = innerRadius * sqrt(Random.nextFloat())
        val x = hmdPos.x + r * kotlin.math.sin(angle)
        val z = hmdPos.z + r * kotlin.math.cos(angle)
        val y = hmdPos.y + hoverHeight + (Random.nextFloat() * 0.2f - 0.1f)
        val zOffset = (Random.nextFloat() * 2f - 1f) * zDeviationRange
        return Vector3(x, y, z + zOffset)
    }

    private fun hasReachedTarget(pos: Vector3, target: Vector3): Boolean {
        val dx = pos.x - target.x
        val dy = pos.y - target.y
        val dz = pos.z - target.z
        return sqrt(dx*dx + dy*dy + dz*dz) < 0.15f
    }

    private fun lerpAngle(a: Float, b: Float, t: Float): Float {
        var diff = b - a
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f
        return a + diff * t
    }

    private fun updateEasedMotionSpeed(
        behavior: FairyBehaviorComponent,
        targetSpeed: Float,
        distanceToTarget: Float,
        dt: Float
    ): Float {
        val arrivalFactor = smoothStep(
            (distanceToTarget / DIRECT_MOVEMENT_SLOWDOWN_RADIUS).coerceIn(0f, 1f)
        ).coerceAtLeast(MIN_MOTION_SPEED_FACTOR)
        val desiredSpeed = targetSpeed * arrivalFactor
        val maxDelta = if (desiredSpeed > behavior.motionSpeed) {
            MOVEMENT_ACCELERATION
        } else {
            MOVEMENT_DECELERATION
        } * dt
        behavior.motionSpeed = approach(behavior.motionSpeed, desiredSpeed, maxDelta)
        return behavior.motionSpeed
    }

    private fun faceTargetPosition(
        behavior: FairyBehaviorComponent,
        from: Vector3,
        target: Vector3,
        turnSpeed: Float,
        dt: Float
    ) {
        val faceDirX = target.x - from.x
        val faceDirZ = target.z - from.z
        if (faceDirX == 0f && faceDirZ == 0f) return
        val targetYaw = atan2(faceDirX, faceDirZ).toDegrees()
        behavior.currentYaw = lerpAngle(
            behavior.currentYaw,
            targetYaw,
            (turnSpeed * dt).coerceIn(0f, 1f)
        )
    }

    private fun faceDirection(
        behavior: FairyBehaviorComponent,
        direction: Vector3,
        turnSpeed: Float,
        dt: Float
    ) {
        if (direction.x == 0f && direction.z == 0f) return
        val targetYaw = atan2(direction.x, direction.z).toDegrees()
        behavior.currentYaw = lerpAngle(
            behavior.currentYaw,
            targetYaw,
            (turnSpeed * dt).coerceIn(0f, 1f)
        )
    }

    private fun applyFairyYaw(
        transform: TransformComponent,
        visualTransform: TransformComponent,
        behavior: FairyBehaviorComponent
    ) {
        val bodyEuler = transform.eulerAngles
        transform.eulerAngles = EulerAngles(
            pitch = bodyEuler.pitch,
            yaw = behavior.currentYaw,
            roll = bodyEuler.roll
        )

        val visualPitch = if (behavior.hasRecordedInitialRotation) behavior.initialPitch else 0f
        val visualRoll = if (behavior.hasRecordedInitialRotation) behavior.initialRoll else 0f
        visualTransform.eulerAngles = EulerAngles(
            pitch = visualPitch,
            yaw = behavior.currentYaw,
            roll = visualRoll
        )
    }

    private fun AvatarController?.facingPolicy(): AnimationFacingPolicy {
        return this?.currentFacingPolicy ?: AnimationFacingPolicy.KEEP_BEHAVIOR
    }

    private fun approach(current: Float, target: Float, maxDelta: Float): Float {
        val delta = target - current
        if (abs(delta) <= maxDelta) return target
        return current + if (delta > 0f) maxDelta else -maxDelta
    }

    private fun smoothStep(value: Float): Float {
        val x = value.coerceIn(0f, 1f)
        return x * x * (3f - 2f * x)
    }

    private fun Float.toDegrees() = this * 180f / Math.PI.toFloat()

    private fun scheduleRandomRestBehavior(
        context: SceneUpdateContext,
        behavior: FairyBehaviorComponent,
        avatarController: AvatarController?,
        fairyPos: Vector3
    ) {
        // Temporarily disable random football interaction while preserving dialogue-triggered
        // play-football. Restore by uncommenting this block after random action instability is fixed.
        /*
        if (tryScheduleRandomPlayFootball(context, fairyPos)) {
            behavior.isWaitingForAnimation = false
            behavior.waitTimer = RANDOM_ACTION_MIN_WAIT_SECONDS
            return
        }
        */

        val idleAnim = avatarController?.requestIdleAnimation()
        if (idleAnim != null) {
            behavior.isWaitingForAnimation = true
            behavior.waitTimer = idleAnim.durationMs / 1000f
        } else {
            behavior.isWaitingForAnimation = false
            behavior.waitTimer = Random.nextFloat() * 2f + 1f
        }
    }

    private fun tryScheduleRandomPlayFootball(context: SceneUpdateContext, fairyPos: Vector3): Boolean {
        if (InteractionActionRuntimeDependencies.lockState.isLocked) return false
        if (Random.nextFloat() > RANDOM_PLAY_FOOTBALL_CHANCE) return false
        val football = InteractionEntityResolver.findObject(context.scene, "football") ?: return false
        val activation = football.components[ResourcePhysicsActivationComponent::class.java]
        if (activation != null && !activation.activated) return false
        val footballTransform = football.components[TransformComponent::class.java] ?: return false
        if (footballTransform.position.y < MIN_RANDOM_ACTION_TARGET_Y) return false
        val footballDistance = horizontalDistance(fairyPos, footballTransform.position)
        PlayFootballPreconditions.updateHorizontalDistance(footballDistance)
        if (footballDistance > PlayFootballPreconditions.MAX_HORIZONTAL_DISTANCE_METERS) {
            return false
        }

        return InteractionActionRuntimeDependencies.requestBus.enqueue(
            InteractionActionRequest(
                actionId = PlayFootballActionController.ACTION_ID,
                objectIds = listOf("football"),
                source = InteractionActionSource.RANDOM
            )
        )
    }

    private fun updatePlayFootballDistance(context: SceneUpdateContext, fairyPos: Vector3) {
        val football = InteractionEntityResolver.findObject(context.scene, "football")
        val footballTransform = football?.components?.get(TransformComponent::class.java)
        if (footballTransform == null) {
            PlayFootballPreconditions.clear()
            return
        }
        PlayFootballPreconditions.updateHorizontalDistance(
            horizontalDistance(fairyPos, footballTransform.position)
        )
    }

    private fun resetBehaviorStateAfterAction(
        fairyEntity: Entity,
        behavior: FairyBehaviorComponent,
        transform: TransformComponent,
        visualTransform: TransformComponent,
        avatarController: AvatarController?
    ) {
        val fairyPos = transform.position
        prepareScriptDrivenFairy(fairyEntity)
        behavior.lastPosition = fairyPos
        behavior.hasRecordedLastPosition = true
        behavior.baseY = fairyPos.y
        behavior.state = FairyState.RANDOM_WAITING
        behavior.currentTarget = null
        behavior.waitTimer = POST_ACTION_IDLE_SECONDS
        behavior.isWaitingForAnimation = false
        behavior.velocity = Vector3.ZERO
        behavior.motionSpeed = 0f
        behavior.inertiaVelocity = Vector3.ZERO
        behavior.isInertiaSliding = false
        behavior.floatTime = 0f

        visualTransform.position = fairyPos
          applyFairyYaw(transform, visualTransform, behavior)
        avatarController?.requestStandbyAnimation()
    }

    private fun ensureCarryingMovementTarget(
        behavior: FairyBehaviorComponent,
        fairyEntity: Entity,
        hmdEntity: Entity,
        transform: TransformComponent,
        hmdPos: Vector3
    ) {
        val currentTarget = behavior.currentTarget
        val needsTarget = currentTarget == null ||
            hasReachedTarget(transform.position, currentTarget) ||
            behavior.state == FairyState.RANDOM_WAITING ||
            behavior.state == FairyState.FOLLOW_HOVERING
        if (!needsTarget) return

        behavior.state = if (horizontalDistance(transform.position, hmdPos) > behavior.outerRadius) {
            FairyState.FOLLOWING
        } else {
            FairyState.RANDOM_MOVING
        }
        behavior.waitTimer = 0f
        behavior.isWaitingForAnimation = false
        behavior.currentTarget = if (behavior.state == FairyState.FOLLOWING) {
            getFollowViewCenterTarget(hmdEntity, fairyEntity, behavior)
        } else {
            getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
        }
    }

    private fun prepareScriptDrivenFairy(entity: Entity) {
        entity.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
        clearPhysicsVelocity(entity)
        entity.components[RigidBodyComponent::class.java]?.let { rigidBody ->
            rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
            rigidBody.isAffectedByGravity = false
        }
        entity.components[CollisionComponent::class.java]?.collisionResponseMode =
            CollisionResponseMode.TRIGGER_LITE
    }

    private fun constrainBySpatialMesh(
        scene: Scene,
        fairyEntity: Entity,
        current: Vector3,
        desiredNext: Vector3
    ): Vector3 {
        if (!desiredNext.isFiniteVector()) return current
        if (!current.isFiniteVector()) return desiredNext

        val delta = desiredNext.minus(current)
        val moveDistance = delta.magnitudeOrZero()
        if (!moveDistance.isFinite() || moveDistance <= MIN_SPATIAL_MESH_CAST_DISTANCE) {
            return desiredNext
        }

        val direction = Vector3(
            delta.x / moveDistance,
            delta.y / moveDistance,
            delta.z / moveDistance
        )
        if (!direction.isFiniteVector()) return desiredNext

        val castShape = spatialMeshCastShape
            ?: ShapeResource.createCapsule(
                height = FAIRY_SPATIAL_MESH_CAST_HEIGHT,
                radius = FAIRY_SPATIAL_MESH_CAST_RADIUS
            ).also { spatialMeshCastShape = it }

        val castResults = runCatching {
            scene.convexCast(
                shape = castShape,
                origin = current,
                orientation = Quat.identity(),
                direction = direction,
                length = moveDistance + SPATIAL_MESH_SKIN_WIDTH,
                hitMode = CollisionCastHitMode.ALL,
                group = CollisionGroup(CollisionGroup.COLLISION_GROUP_DEFAULT),
                referenceEntity = fairyEntity.getParent()
            ).results
        }.getOrElse {
            return desiredNext
        }

        val hit = castResults
            .asSequence()
            .filter { result ->
                result.entity != fairyEntity &&
                    result.distance.isFinite() &&
                    result.distance >= 0f &&
                    SpatialMeshRuntimeDependencies.query.getAnchorUUID(result.entity) != null
            }
            .minByOrNull { it.distance }
            ?: return desiredNext

        val allowedDistance = (hit.distance - SPATIAL_MESH_SKIN_WIDTH)
            .coerceIn(0f, moveDistance)
        return Vector3(
            current.x + direction.x * allowedDistance,
            current.y + direction.y * allowedDistance,
            current.z + direction.z * allowedDistance
        )
    }

    private fun clearPhysicsVelocity(entity: Entity) {
        val velocity = entity.components[PhysicsVelocityComponent::class.java]
            ?: PhysicsVelocityComponent().also { entity.components.set(it) }
        velocity.linearVelocity = Vector3.ZERO
        velocity.angularVelocity = Vector3.ZERO
    }

    private fun Vector3.minus(other: Vector3): Vector3 {
        return Vector3(x - other.x, y - other.y, z - other.z)
    }

    private fun Vector3.magnitudeOrZero(): Float {
        return sqrt(x * x + y * y + z * z)
    }

    private fun Vector3.isFiniteVector(): Boolean {
        return x.isFinite() && y.isFinite() && z.isFinite()
    }

    private fun horizontalDistance(a: Vector3, b: Vector3): Float {
        val dx = a.x - b.x
        val dz = a.z - b.z
        return sqrt(dx * dx + dz * dz)
    }

    private fun horizontalDirection(from: Vector3, to: Vector3): Vector3? {
        val dx = to.x - from.x
        val dz = to.z - from.z
        val length = sqrt(dx * dx + dz * dz)
        if (length <= 0.001f) return null
        return Vector3(dx / length, 0f, dz / length)
    }

    private fun viewForwardFromYaw(yawDegrees: Float): Vector3 {
        val radians = yawDegrees * Math.PI.toFloat() / 180f
        return Vector3(-sin(radians), 0f, -cos(radians))
    }

    private fun distance(a: Vector3, b: Vector3): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        val dz = a.z - b.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    private fun direction(from: Vector3, to: Vector3): Vector3? {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val dz = to.z - from.z
        val length = sqrt(dx * dx + dy * dy + dz * dz)
        if (length <= 0.001f) return null
        return Vector3(dx / length, dy / length, dz / length)
    }

    private fun resolveHmdEntity(context: SceneUpdateContext): Entity? {
        val cached = cachedHmdEntity
        if (cached != null &&
            cached.components[HMDTagComponent::class.java] != null &&
            cached.components[TransformComponent::class.java] != null
        ) {
            return cached
        }
        val resolved = context.scene.queryEntity(hmdCondition).firstOrNull()
        cachedHmdEntity = resolved
        return resolved
    }

    private fun resolveFairyEntities(context: SceneUpdateContext): List<Entity> {
        if (cachedFairyEntities.isNotEmpty() && cachedFairyEntities.all(::isUsableFairyEntity)) {
            return cachedFairyEntities
        }
        val resolved = context.scene.queryEntity(fairyCondition).filter(::isUsableFairyEntity)
        cachedFairyEntities = resolved
        return resolved
    }

    private fun isUsableFairyEntity(entity: Entity): Boolean {
        return entity.components[FairyBehaviorComponent::class.java] != null &&
            entity.components[TransformComponent::class.java] != null
    }

    private companion object {
        private const val RANDOM_PLAY_FOOTBALL_CHANCE = 0.18f
        private const val RANDOM_ACTION_MIN_WAIT_SECONDS = 1.5f
        private const val MIN_RANDOM_ACTION_TARGET_Y = -0.5f
        private const val MAX_CARRYING_DIRECT_SPEED = 0.65f
        private const val FOLLOWING_SPEED_SCALE = 0.85f
        private const val MOVEMENT_ACCELERATION = 1.4f
        private const val MOVEMENT_DECELERATION = 2.1f
        private const val MIN_MOTION_SPEED_FACTOR = 0.14f
        private const val MOVEMENT_TURN_SPEED = 5.0f
        private const val FOLLOWING_LOOK_TURN_SPEED = 6.5f
          private const val ANIMATION_FACE_PLAYER_TURN_SPEED = 10.0f
        private const val FOLLOW_VIEW_CENTER_DISTANCE = 0.9f
        private const val DIRECT_MOVEMENT_SLOWDOWN_RADIUS = 0.8f
        private const val DIRECT_IDLE_VERTICAL_SPEED = 0.25f
        private const val FAIRY_SPATIAL_MESH_CAST_HEIGHT = 0.34f
        private const val FAIRY_SPATIAL_MESH_CAST_RADIUS = 0.14f
        private const val SPATIAL_MESH_SKIN_WIDTH = 0.04f
        private const val MIN_SPATIAL_MESH_CAST_DISTANCE = 0.001f
        private const val FOLLOW_TARGET_REFRESH_MARGIN = 0.45f
        private const val RESIDENCE_IDLE_REFRESH_SECONDS = 2.8f
        private const val POST_ACTION_IDLE_SECONDS = 1.2f
        private const val DEFAULT_BEHAVIOR_FAIRY_ACTOR_ID = "fairy"
        private val DEFAULT_HMD_FORWARD = Vector3(0f, 0f, -1f)
    }
}
