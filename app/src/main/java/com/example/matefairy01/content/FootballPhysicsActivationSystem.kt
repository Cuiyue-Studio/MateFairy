package com.example.matefairy01.content

import com.example.matefairy01.interaction.InteractionObjectComponent
import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.EntityQueryCondition
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.System
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.simulation.CollisionCastHitMode
import com.pico.spatial.core.ecs.simulation.CollisionGroup
import com.pico.spatial.core.math.Vector3

class ResourcePhysicsActivationComponent(
    val radius: Float = 0.11f,
    val floorOffset: Float = radius,
    val minWaitSeconds: Float = 1.5f,
    val rayStartHeight: Float = 0.6f,
    val rayLength: Float = 1.5f,
) : Component() {
    var elapsedSeconds: Float = 0f
    var activated: Boolean = false
    var lastNoFloorReportSeconds: Float = 0f
    var lastActivatedReportSeconds: Float = 0f
    var lastSafePosition: Vector3? = null
}

class ResourcePhysicsActivationSystem : System() {
    private val activationCondition =
        EntityQueryCondition.hasComponent(ResourcePhysicsActivationComponent::class.java)

    override fun update(context: SceneUpdateContext) {
        val dt = context.deltaTime
        if (dt <= 0f) return

        context.scene.queryEntity(activationCondition).forEach { resource ->
            val activation = resource.components[ResourcePhysicsActivationComponent::class.java]
                ?: return@forEach
            activation.elapsedSeconds += dt

            val transform = resource.components[TransformComponent::class.java] ?: return@forEach
            val rigidBody = resource.components[RigidBodyComponent::class.java] ?: return@forEach
            val position = transform.position
            val objectId = resource.components[InteractionObjectComponent::class.java]?.objectId
                ?: resource.getName()

            if (activation.activated) {
                if (position.y < FALL_RESET_Y && isSportsBall(objectId)) {
                    resetResourceToSafeSuspendedPose(resource, transform, rigidBody, activation)
                    return@forEach
                }
                if (shouldReportActivatedResource(objectId, activation.elapsedSeconds, activation.lastActivatedReportSeconds, position.y)) {
                    activation.lastActivatedReportSeconds = activation.elapsedSeconds
                }
                return@forEach
            }

            if (isSportsBall(objectId) && position.y < PRE_ACTIVATION_VISIBLE_Y) {
                transform.position = Vector3(position.x, PRE_ACTIVATION_VISIBLE_Y, position.z)
                clearPhysicsMotion(resource, rigidBody)
            }

            if (activation.elapsedSeconds < activation.minWaitSeconds) return@forEach

            val rayBasePosition = transform.position
            val origin = Vector3(
                rayBasePosition.x,
                rayBasePosition.y + activation.rayStartHeight,
                rayBasePosition.z
            )

            val hit = context.scene.rayCast(
                origin = origin,
                direction = Vector3(0f, -1f, 0f),
                length = activation.rayLength,
                hitMode = CollisionCastHitMode.ALL,
                group = CollisionGroup(CollisionGroup.COLLISION_GROUP_ALL),
            ).results.firstOrNull { result ->
                result.entity != resource && !isDescendantOf(result.entity, resource)
            }

            if (hit == null) {
                val currentPosition = transform.position
                if (isSportsBall(objectId) && currentPosition.y < PRE_ACTIVATION_VISIBLE_Y) {
                    transform.position = Vector3(currentPosition.x, PRE_ACTIVATION_VISIBLE_Y, currentPosition.z)
                    clearPhysicsMotion(resource, rigidBody)
                }
                if (activation.elapsedSeconds - activation.lastNoFloorReportSeconds >= 2f) {
                    activation.lastNoFloorReportSeconds = activation.elapsedSeconds
                }
                return@forEach
            }

            val floorY = origin.y - hit.distance
            val safeY = floorY + activation.floorOffset + 0.03f
            val safePosition = Vector3(rayBasePosition.x, safeY, rayBasePosition.z)
            if (position.y < safeY) {
                transform.position = safePosition
            }
            activation.lastSafePosition = safePosition

            rigidBody.isAffectedByGravity = true
            activation.activated = true
        }
    }

    private fun isDescendantOf(entity: Entity, possibleAncestor: Entity): Boolean {
        var current = entity.getParent()
        while (current != null) {
            if (current == possibleAncestor) return true
            current = current.getParent()
        }
        return false
    }

    private fun shouldReportActivatedResource(
        objectId: String,
        elapsedSeconds: Float,
        lastReportSeconds: Float,
        y: Float
    ): Boolean {
        if (objectId != "football" && objectId != "basketball") return false
        return y < -0.5f || elapsedSeconds - lastReportSeconds >= 1f
    }

    private fun resetResourceToSafeSuspendedPose(
        resource: Entity,
        transform: TransformComponent,
        rigidBody: RigidBodyComponent,
        activation: ResourcePhysicsActivationComponent
    ) {
        val fallback = activation.lastSafePosition
            ?: Vector3(transform.position.x, PRE_ACTIVATION_VISIBLE_Y, transform.position.z)
        transform.position = fallback
        activation.activated = false
        activation.lastNoFloorReportSeconds = activation.elapsedSeconds
        clearPhysicsMotion(resource, rigidBody)
    }

    private fun clearPhysicsMotion(resource: Entity, rigidBody: RigidBodyComponent) {
        rigidBody.isAffectedByGravity = false
        resource.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
        resource.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO
    }

    private fun isSportsBall(objectId: String): Boolean {
        return objectId == "football" || objectId == "basketball"
    }

    private companion object {
        private const val PRE_ACTIVATION_VISIBLE_Y = 0.65f
        private const val FALL_RESET_Y = -0.5f
    }
}
