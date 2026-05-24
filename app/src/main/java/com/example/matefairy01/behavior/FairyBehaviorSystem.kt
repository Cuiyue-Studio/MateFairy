package com.example.matefairy01.behavior

import com.pico.spatial.core.ecs.System
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.EntityQueryCondition
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.core.math.EulerAngles
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

    override fun update(context: SceneUpdateContext) {
        val dt = context.deltaTime

        val hmdEntity = resolveHmdEntity(context) ?: return
        // SDK 提供了 getGlobalPosition() 或者在 Scene 中可以根据层级计算，但 HMD 通常就是世界坐标
        val hmdTransform = hmdEntity.components[TransformComponent::class.java] ?: return
        val hmdPos = hmdTransform.position
        val avatarController = BehaviorRuntimeDependencies.avatarController

        val fairyEntities = resolveFairyEntities(context)
        if (fairyEntities.isEmpty()) return
        
        for (fairyEntity in fairyEntities) {
            val behavior = fairyEntity.components[FairyBehaviorComponent::class.java]!!
            // 精灵的 position 需要用其自身的绝对世界坐标，因为如果在 Wrapper 中，可能 Transform 的 position 是局部坐标
            val transform = fairyEntity.components[TransformComponent::class.java] ?: continue
            
            // 因为我们在 Wrapper 模式下，FairyWrapper 的位移是 0，所以它的世界坐标等于局部坐标
            // 所以直接使用 transform.position 即可，这也是之前正常工作的方式。
            val fairyPos = transform.position
            
            // Calculate 3D distance
            val dx = fairyPos.x - hmdPos.x
            val dy = fairyPos.y - hmdPos.y
            val dz = fairyPos.z - hmdPos.z
            val distance3D = sqrt(dx * dx + dy * dy + dz * dz)
            
            // Calculate horizontal distance (X-Z plane) for follow logic
            val distance2D = sqrt(dx * dx + dz * dz)
            
            // ============================================
            // Handle inertia sliding
            // If we were sliding, continue decelerating
            // ============================================
            if (behavior.isInertiaSliding) {
                val speed = sqrt(
                    behavior.inertiaVelocity.x * behavior.inertiaVelocity.x +
                    behavior.inertiaVelocity.y * behavior.inertiaVelocity.y +
                    behavior.inertiaVelocity.z * behavior.inertiaVelocity.z
                )

                if (speed > behavior.inertiaStopThreshold) {
                    // Apply deceleration (friction)
                    val decelFactor = 1f - (behavior.inertiaDeceleration * dt)
                    behavior.inertiaVelocity = Vector3(
                        behavior.inertiaVelocity.x * decelFactor,
                        behavior.inertiaVelocity.y * decelFactor,
                        behavior.inertiaVelocity.z * decelFactor
                    )

                    // Move with inertia
                    val newPos = Vector3(
                        transform.position.x + behavior.inertiaVelocity.x * dt,
                        transform.position.y + behavior.inertiaVelocity.y * dt,
                        transform.position.z + behavior.inertiaVelocity.z * dt
                    )
                    transform.position = newPos

                    // Continue facing the inertia direction
                    if (behavior.inertiaVelocity.x != 0f || behavior.inertiaVelocity.z != 0f) {
                        val inertiaYaw = atan2(behavior.inertiaVelocity.x, behavior.inertiaVelocity.z).toDegrees() + 180f
                        behavior.currentYaw = lerpAngle(behavior.currentYaw, inertiaYaw, 3.0f * dt)
                    }

                    // Apply rotation (no tilt, just preserve upright pose)
                    val pitch = if (behavior.hasRecordedInitialRotation) behavior.initialPitch else 0f
                    val roll = if (behavior.hasRecordedInitialRotation) behavior.initialRoll else 0f
                    transform.eulerAngles = EulerAngles(pitch = pitch, yaw = behavior.currentYaw, roll = roll)

                    // Skip normal state processing while sliding
                    continue
                } else {
                    // Inertia exhausted, stop sliding
                    behavior.isInertiaSliding = false
                    behavior.inertiaVelocity = Vector3(0f, 0f, 0f)
                }
            }
            
            // State machine update
            when (behavior.state) {
                FairyState.FOLLOW_HOVERING -> {
                    // Follow-hovering state: fairy reached player after following, now hovering and facing player
                    if (behavior.waitTimer <= 0f) {
                        behavior.waitTimer = Random.nextFloat() * 1f + 1f // Hover 1-2 seconds
                    }
                    behavior.waitTimer -= dt
                    if (behavior.waitTimer <= 0f) {
                        // Hover finished, resume random movement
                        behavior.state = FairyState.RANDOM_MOVING
                        behavior.waitTimer = 0f
                        behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
                        avatarController?.requestMovingAnimation()
                    }
                }
                
                FairyState.RANDOM_MOVING -> {
                    // Random movement state
                    if (distance2D > behavior.outerRadius) {
                        // Trigger follow mode if too far horizontally
                        triggerInertiaSlide(behavior) // Start inertia slide before state change
                        behavior.state = FairyState.FOLLOWING
                        behavior.waitTimer = 0f
                        behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
                        avatarController?.requestMovingAnimation()
                    } else {
                        // Normal random movement inside the inner/buffer zone
                        if (behavior.currentTarget == null || hasReachedTarget(fairyPos, behavior.currentTarget!!)) {
                            // Reached target, trigger inertia slide then switch to waiting
                            triggerInertiaSlide(behavior)
                            behavior.state = FairyState.RANDOM_WAITING
                            
                            // 尝试播放静止时动画
                            val idleAnim = avatarController?.requestIdleAnimation()
                            if (idleAnim != null) {
                                behavior.isWaitingForAnimation = true
                                behavior.waitTimer = idleAnim.durationMs / 1000f
                            } else {
                                behavior.isWaitingForAnimation = false
                                behavior.waitTimer = Random.nextFloat() * 2f + 1f // Wait 1-3 seconds
                            }
                            
                            behavior.baseY = transform.position.y
                            behavior.velocity = Vector3(0f, 0f, 0f)
                        }
                    }
                }
                
                FairyState.RANDOM_WAITING -> {
                    // Random waiting state: floating in place, NOT facing player
                    // 如果没有播放动画，且时间到了，就重置时间（旧逻辑，这里可以去掉，因为进入时已经设置了）
                    if (!behavior.isWaitingForAnimation && behavior.waitTimer <= 0f) {
                        behavior.waitTimer = Random.nextFloat() * 2f + 1f
                    }
                    
                    behavior.waitTimer -= dt
                    
                    if (behavior.waitTimer <= 0f) {
                        // Wait finished, resume random movement
                        behavior.state = FairyState.RANDOM_MOVING
                        behavior.waitTimer = 0f
                        behavior.isWaitingForAnimation = false
                        behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
                        avatarController?.requestMovingAnimation()
                    }
                    
                    // Check if player moved too far during random waiting
                    if (distance2D > behavior.outerRadius) {
                        behavior.state = FairyState.FOLLOWING
                        behavior.waitTimer = 0f
                        behavior.isWaitingForAnimation = false
                        behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)
                        avatarController?.requestMovingAnimation()
                    }
                }
                
                FairyState.FOLLOWING -> {
                    // Following mode - continuously update target to player position
                    behavior.currentTarget = getRandomTargetInInnerRadius(hmdPos, behavior.innerRadius, behavior.hoverHeight, behavior.zDeviationRange)

                    if (distance2D <= behavior.innerRadius) {
                        // Reached the inner circle, trigger inertia slide then switch to hovering
                        triggerInertiaSlide(behavior)
                        behavior.state = FairyState.FOLLOW_HOVERING
                        behavior.waitTimer = 0f
                        behavior.velocity = Vector3(0f, 0f, 0f)
                        behavior.baseY = transform.position.y
                        
                        // 跟随悬停也可以播放一个静止动画
                        val idleAnim = avatarController?.requestIdleAnimation()
                        if (idleAnim != null) {
                            behavior.isWaitingForAnimation = true
                            behavior.waitTimer = idleAnim.durationMs / 1000f
                        }
                    }
                }
            }
            
            // Movement and rotation logic
            when (behavior.state) {
                FairyState.RANDOM_MOVING, FairyState.FOLLOWING -> {
                    // Moving states: move towards target and face movement direction
                    val targetPos = behavior.currentTarget
                    if (targetPos != null) {
                        val targetSpeed = if (behavior.state == FairyState.FOLLOWING) behavior.followSpeed else behavior.speed
                        
                        val dirX = targetPos.x - fairyPos.x
                        val dirY = targetPos.y - fairyPos.y
                        val dirZ = targetPos.z - fairyPos.z
                        val dist = sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ)
                        
                        if (dist > 0.05f) {
                            // Desired velocity
                            val desiredVx = (dirX / dist) * targetSpeed
                            val desiredVy = (dirY / dist) * targetSpeed
                            val desiredVz = (dirZ / dist) * targetSpeed
                            
                            // Steering force (interpolate velocity)
                            val turnFactor = 2.5f * dt
                            behavior.velocity = Vector3(
                                behavior.velocity.x + (desiredVx - behavior.velocity.x) * turnFactor,
                                behavior.velocity.y + (desiredVy - behavior.velocity.y) * turnFactor,
                                behavior.velocity.z + (desiredVz - behavior.velocity.z) * turnFactor
                            )
                            
                            val newPos = Vector3(
                                transform.position.x + behavior.velocity.x * dt,
                                transform.position.y + behavior.velocity.y * dt,
                                transform.position.z + behavior.velocity.z * dt
                            )
                            transform.position = newPos
                            
                            // Smooth Rotation based on current velocity (face movement direction)
                            // CRITICAL: Preserve initial pitch and roll to keep robot upright!
                            if (behavior.velocity.x != 0f || behavior.velocity.z != 0f) {
                                // 移除 +180f 以修正精灵前后朝向反了的问题
                                val targetYaw = atan2(behavior.velocity.x, behavior.velocity.z).toDegrees()
                                behavior.currentYaw = lerpAngle(behavior.currentYaw, targetYaw, 5.0f * dt)
                                
                                // Apply rotation: use initial pitch/roll for upright pose, current yaw for direction
                                val pitch = if (behavior.hasRecordedInitialRotation) behavior.initialPitch else 0f
                                val roll = if (behavior.hasRecordedInitialRotation) behavior.initialRoll else 0f
                                transform.eulerAngles = EulerAngles(pitch = pitch, yaw = behavior.currentYaw, roll = roll)
                            }
                        } else {
                            // Slow down to stop - trigger inertia slide
                            triggerInertiaSlide(behavior)
                            behavior.velocity = Vector3(0f, 0f, 0f)
                        }
                    }
                }
                
                FairyState.RANDOM_WAITING -> {
                    // Random waiting: apply floating effect, keep current rotation (don't face player)
                    behavior.floatTime += dt
                    
                    val floatOffset = behavior.floatAmplitude * sin(behavior.floatTime * behavior.floatSpeed) * 
                                     abs(sin(behavior.floatTime * behavior.floatSpeed * 0.7f))
                    
                    // Note: transform.position is local to parent.
                    // We must use transform.position.x and z to avoid teleporting if parent moves.
                    // baseY was recorded using global Y, so we must adjust it to local Y.
                    // To keep it simple, baseY should be recorded as local Y. Let's fix that.
                    val newPos = Vector3(
                        transform.position.x,
                        behavior.baseY + floatOffset,
                        transform.position.z
                    )
                    transform.position = newPos
                    
                    // Keep current yaw with initial pitch/roll, no rotation towards player
                    val pitch = if (behavior.hasRecordedInitialRotation) behavior.initialPitch else 0f
                    val roll = if (behavior.hasRecordedInitialRotation) behavior.initialRoll else 0f
                    transform.eulerAngles = EulerAngles(pitch = pitch, yaw = behavior.currentYaw, roll = roll)
                }
                
                FairyState.FOLLOW_HOVERING -> {
                    // Follow-hovering: apply floating effect and face player
                    behavior.floatTime += dt
                    
                    val floatOffset = behavior.floatAmplitude * sin(behavior.floatTime * behavior.floatSpeed) * 
                                     abs(sin(behavior.floatTime * behavior.floatSpeed * 0.7f))
                    
                    val newPos = Vector3(
                        transform.position.x,
                        behavior.baseY + floatOffset,
                        transform.position.z
                    )
                    transform.position = newPos
                    
                    // Face the player during follow-hovering
                    // CRITICAL: Preserve initial pitch and roll to keep robot upright!
                    val faceDirX = hmdPos.x - fairyPos.x
                    val faceDirZ = hmdPos.z - fairyPos.z
                    if (faceDirX != 0f || faceDirZ != 0f) {
                        // 移除 +180f 以修正精灵前后朝向反了的问题
                        val targetYaw = atan2(faceDirX, faceDirZ).toDegrees()
                        behavior.currentYaw = lerpAngle(behavior.currentYaw, targetYaw, 3.0f * dt)
                    }
                    
                    val pitch = if (behavior.hasRecordedInitialRotation) behavior.initialPitch else 0f
                    val roll = if (behavior.hasRecordedInitialRotation) behavior.initialRoll else 0f
                    transform.eulerAngles = EulerAngles(pitch = pitch, yaw = behavior.currentYaw, roll = roll)
                }
            }
        }
    }
    
    /**
     * Trigger inertia slide when stopping or changing state.
     * Copies current velocity to inertiaVelocity and starts deceleration.
     */
    private fun triggerInertiaSlide(behavior: FairyBehaviorComponent) {
        val speed = sqrt(
            behavior.velocity.x * behavior.velocity.x +
            behavior.velocity.y * behavior.velocity.y +
            behavior.velocity.z * behavior.velocity.z
        )

        if (speed > behavior.inertiaStopThreshold) {
            behavior.inertiaVelocity = Vector3(
                behavior.velocity.x,
                behavior.velocity.y,
                behavior.velocity.z
            )
            behavior.isInertiaSliding = true
        }
    }
    
    private fun getRandomTargetInInnerRadius(hmdPos: Vector3, innerRadius: Float, hoverHeight: Float, zDeviationRange: Float): Vector3 {
        // 随机一个相对于头显的水平角度
        val angle = Random.nextFloat() * 2f * Math.PI.toFloat()
        
        // 恢复实心圆盘分布逻辑：精灵可以在 0 到 innerRadius 的任何位置飞行，甚至贴近玩家
        val r = innerRadius * sqrt(Random.nextFloat())
        
        val x = hmdPos.x + r * kotlin.math.sin(angle)
        val z = hmdPos.z + r * kotlin.math.cos(angle)
        
        // 计算高度：相对于头显的真实高度，不要太大的随机浮动，保持在头显视平线附近
        // hmdPos.y 是头显的真实世界高度（例如 1.6m）
        // hoverHeight 是相对高度（例如 -0.1m 代表比眼睛低 10cm）
        val y = hmdPos.y + hoverHeight + (Random.nextFloat() * 0.2f - 0.1f) // 上下最多浮动 10cm
        
        // Z 轴额外随机偏移（可选，不需要太大）
        val zOffset = (Random.nextFloat() * 2f - 1f) * zDeviationRange
        
        return Vector3(x, y, z + zOffset)
    }
    
    private fun hasReachedTarget(pos: Vector3, target: Vector3): Boolean {
        val dx = pos.x - target.x
        val dy = pos.y - target.y
        val dz = pos.z - target.z
        return sqrt(dx*dx + dy*dy + dz*dz) < 0.05f
    }
    
    private fun lerpAngle(a: Float, b: Float, t: Float): Float {
        var diff = b - a
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f
        return a + diff * t
    }
    
    private fun Float.toDegrees() = this * 180f / Math.PI.toFloat()

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
}
