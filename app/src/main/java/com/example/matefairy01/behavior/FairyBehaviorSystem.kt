package com.example.matefairy01.behavior

import com.pico.spatial.core.ecs.System
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.EntityQueryCondition
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.simulation.CollisionCastHitMode
import com.pico.spatial.core.ecs.simulation.CollisionGroup
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.core.math.EulerAngles
import com.example.matefairy01.interaction.FairyActionLockComponent
import com.example.matefairy01.interaction.InteractionActionRequest
import com.example.matefairy01.interaction.InteractionActionRuntimeDependencies
import com.example.matefairy01.interaction.InteractionActionSource
import com.example.matefairy01.interaction.InteractionEntityResolver
import com.example.matefairy01.interaction.PlayFootballActionController
import com.example.matefairy01.interaction.PlayFootballPreconditions
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.sin
import kotlin.math.abs
import kotlin.random.Random

class FairyBehaviorSystem : System() {
    private val hmdCondition = EntityQueryCondition.hasComponent(HMDTagComponent::class.java)
    private val fairyCondition =
        EntityQueryCondition.hasComponent(FairyBehaviorComponent::class.java)

    private var cachedHmdEntity: Entity? = null
    private var cachedFairyEntities: List<Entity> = emptyList()
    private var wasFollowEnabled: Boolean = true

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
            val physicsForce = fairyEntity.components[PhysicsForceComponent::class.java]
            val visualTransform =
                behavior.visualEntity?.components?.get(TransformComponent::class.java) ?: transform

            if (
                fairyEntity.components[FairyActionLockComponent::class.java] != null ||
                !followEnabled
            ) {
                visualTransform.position = transform.position
                visualTransform.eulerAngles = transform.eulerAngles
                behavior.lastPosition = transform.position
                behavior.hasRecordedLastPosition = true
                continue
            }

            val fairyPos = transform.position
            updatePlayFootballDistance(context, fairyPos)
            if (followJustRestored) {
                reconcileAfterAction(behavior, fairyPos, hmdPos, avatarController)
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

            // Calculate actual velocity based on position delta
            val safeDt = dt.coerceAtLeast(0.001f)
            val actualVelocity = Vector3(
                (fairyPos.x - behavior.lastPosition.x) / safeDt,
                (fairyPos.y - behavior.lastPosition.y) / safeDt,
                (fairyPos.z - behavior.lastPosition.z) / safeDt
            )
            behavior.lastPosition = fairyPos

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
                        behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
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
                        behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
                        avatarController?.requestMovingAnimation()
                    }
                }

                FairyState.FOLLOWING -> {
                    behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)

                    if (distance2D <= behavior.innerRadius) {
                        behavior.state = FairyState.FOLLOW_HOVERING
                        behavior.waitTimer = 0f
                        behavior.baseY = transform.position.y

                        scheduleRandomRestBehavior(context, behavior, avatarController, fairyPos)
                    }
                }
            }

            // Movement and physics logic
            var totalForce = Vector3(0f, 0f, 0f)
            val baseStiffness = 30.0f

            when (behavior.state) {
                FairyState.RANDOM_MOVING, FairyState.FOLLOWING -> {
                    val targetPos = behavior.currentTarget
                    if (targetPos != null) {
                        val targetSpeed = if (behavior.state == FairyState.FOLLOWING) behavior.followSpeed else behavior.speed

                        val dirX = targetPos.x - fairyPos.x
                        val dirY = targetPos.y - fairyPos.y
                        val dirZ = targetPos.z - fairyPos.z
                        val dist = sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ)

                        if (dist > 0.05f) {
                            // Compute desired velocity towards target
                            val desiredVx = (dirX / dist) * targetSpeed
                            val desiredVy = (dirY / dist) * targetSpeed
                            val desiredVz = (dirZ / dist) * targetSpeed

                            // PD control for velocity
                            val pGain = 15.0f
                            totalForce = Vector3(
                                (desiredVx - actualVelocity.x) * pGain,
                                (desiredVy - actualVelocity.y) * pGain,
                                (desiredVz - actualVelocity.z) * pGain
                            )

                            // Use intended travel direction for heading; physics jitter must not drive yaw.
                            if (dirX != 0f || dirZ != 0f) {
                                val targetYaw = atan2(dirX, dirZ).toDegrees()
                                behavior.currentYaw = lerpAngle(behavior.currentYaw, targetYaw, 5.0f * dt)
                            }
                        }
                    }
                }

                FairyState.RANDOM_WAITING, FairyState.FOLLOW_HOVERING -> {
                    // Floating effect
                    behavior.floatTime += dt
                    val floatOffset = behavior.floatAmplitude * sin(behavior.floatTime * behavior.floatSpeed) *
                                     abs(sin(behavior.floatTime * behavior.floatSpeed * 0.7f))

                    // Use a spring force to keep it at baseY + floatOffset
                    val targetY = behavior.baseY + floatOffset
                    val errorY = targetY - fairyPos.y

                    // Spring force: F = k * x - d * v
                    val k = baseStiffness
                    val d = 10.0f
                    totalForce = Vector3(
                        -actualVelocity.x * d, // damping X
                        (errorY * k) - (actualVelocity.y * d), // spring Y
                        -actualVelocity.z * d  // damping Z
                    )

                    if (behavior.state == FairyState.FOLLOW_HOVERING) {
                        // Face the player
                        val faceDirX = hmdPos.x - fairyPos.x
                        val faceDirZ = hmdPos.z - fairyPos.z
                        if (faceDirX != 0f || faceDirZ != 0f) {
                            val targetYaw = atan2(faceDirX, faceDirZ).toDegrees()
                            behavior.currentYaw = lerpAngle(behavior.currentYaw, targetYaw, 3.0f * dt)
                        }
                    }
                }
            }

            // ----------------------------------------------------
            // Raycast Hovering & Obstacle Avoidance
            // ----------------------------------------------------

            // Raycast Down to avoid floor jitter (suspension spring)
            val raycastDown = context.scene.rayCast(
                origin = fairyPos,
                direction = Vector3(0f, -1f, 0f),
                length = 0.6f,
                hitMode = CollisionCastHitMode.NEAREST,
                group = CollisionGroup(CollisionGroup.COLLISION_GROUP_ALL)
            )

            val floorHit = raycastDown.results.firstOrNull { it.entity != fairyEntity }
            if (floorHit != null) {
                val hit = floorHit
                val floorDist = hit.distance
                // If the floor is too close (< 0.4m), apply strong upward suspension force
                val minHoverHeight = 0.4f
                if (floorDist < minHoverHeight && floorDist > 0f) {
                    val pushUpError = minHoverHeight - floorDist
                    // Add extra upward force to prevent hitting the floor
                    val suspensionForce = pushUpError * 100.0f
                    totalForce = Vector3(totalForce.x, totalForce.y + suspensionForce, totalForce.z)
                }
            }

            // Apply physics force
            if (physicsForce != null) {
                physicsForce.force = totalForce
            }

            // Keep the visual GLB separated from the physics proxy so rigid body updates cannot
            // overwrite the model scale or animation hierarchy.
            visualTransform.position = fairyPos

            // Apply visual rotation manually. The physics capsule itself stays rotation-locked.
            val pitch = if (behavior.hasRecordedInitialRotation) behavior.initialPitch else 0f
            val roll = if (behavior.hasRecordedInitialRotation) behavior.initialRoll else 0f
            visualTransform.eulerAngles = EulerAngles(pitch = pitch, yaw = behavior.currentYaw, roll = roll)
        }
        wasFollowEnabled = followEnabled
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
        return sqrt(dx*dx + dy*dy + dz*dz) < 0.15f // Increased tolerance slightly for physics
    }

    private fun lerpAngle(a: Float, b: Float, t: Float): Float {
        var diff = b - a
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f
        return a + diff * t
    }

    private fun Float.toDegrees() = this * 180f / Math.PI.toFloat()

    private fun scheduleRandomRestBehavior(
        context: SceneUpdateContext,
        behavior: FairyBehaviorComponent,
        avatarController: com.example.matefairy01.avatar.AvatarController?,
        fairyPos: Vector3
    ) {
        if (tryScheduleRandomPlayFootball(context, fairyPos)) {
            behavior.isWaitingForAnimation = false
            behavior.waitTimer = RANDOM_ACTION_MIN_WAIT_SECONDS
            return
        }

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
        val footballTransform = football.components[TransformComponent::class.java] ?: return false
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

    private fun reconcileAfterAction(
        behavior: FairyBehaviorComponent,
        fairyPos: Vector3,
        hmdPos: Vector3,
        avatarController: com.example.matefairy01.avatar.AvatarController?
    ) {
        behavior.lastPosition = fairyPos
        behavior.hasRecordedLastPosition = true
        behavior.baseY = fairyPos.y
        behavior.waitTimer = 0f
        behavior.isWaitingForAnimation = false

        val distance2D = horizontalDistance(fairyPos, hmdPos)
        if (distance2D > behavior.outerRadius) {
            behavior.state = FairyState.FOLLOWING
            behavior.currentTarget = getRandomTargetInInnerRadius(
                hmdPos,
                behavior.innerRadius,
                behavior.hoverHeight,
                behavior.zDeviationRange
            )
            avatarController?.requestMovingAnimation()
        } else {
            behavior.state = FairyState.RANDOM_MOVING
            behavior.currentTarget = getRandomTargetInInnerRadius(
                hmdPos,
                behavior.innerRadius,
                behavior.hoverHeight,
                behavior.zDeviationRange
            )
        }
    }

    private fun horizontalDistance(a: Vector3, b: Vector3): Float {
        val dx = a.x - b.x
        val dz = a.z - b.z
        return sqrt(dx * dx + dz * dz)
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
    }
}
