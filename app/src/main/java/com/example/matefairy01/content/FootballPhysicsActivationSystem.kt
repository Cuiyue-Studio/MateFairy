package com.example.matefairy01.content

import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.EntityQueryCondition
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.System
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.simulation.CollisionCastHitMode
import com.pico.spatial.core.ecs.simulation.CollisionGroup
import com.pico.spatial.core.math.Vector3

class FootballPhysicsActivationComponent(
    val radius: Float = 0.11f,
    val minWaitSeconds: Float = 1.5f,
    val rayStartHeight: Float = 0.6f,
    val rayLength: Float = 1.5f,
) : Component() {
    var elapsedSeconds: Float = 0f
    var activated: Boolean = false
}

class FootballPhysicsActivationSystem : System() {
    private val footballCondition =
        EntityQueryCondition.hasComponent(FootballPhysicsActivationComponent::class.java)

    override fun update(context: SceneUpdateContext) {
        val dt = context.deltaTime
        if (dt <= 0f) return

        context.scene.queryEntity(footballCondition).forEach { football ->
            val activation = football.components[FootballPhysicsActivationComponent::class.java]
                ?: return@forEach
            if (activation.activated) return@forEach

            activation.elapsedSeconds += dt
            if (activation.elapsedSeconds < activation.minWaitSeconds) return@forEach

            val transform = football.components[TransformComponent::class.java] ?: return@forEach
            val rigidBody = football.components[RigidBodyComponent::class.java] ?: return@forEach
            val position = transform.position
            val origin = Vector3(position.x, position.y + activation.rayStartHeight, position.z)

            val hit = context.scene.rayCast(
                origin = origin,
                direction = Vector3(0f, -1f, 0f),
                length = activation.rayLength,
                hitMode = CollisionCastHitMode.ALL,
                group = CollisionGroup(CollisionGroup.COLLISION_GROUP_ALL),
            ).results.firstOrNull { result ->
                result.entity != football && !isDescendantOf(result.entity, football)
            } ?: return@forEach

            val floorY = origin.y - hit.distance
            val safeY = floorY + activation.radius + 0.03f
            if (position.y < safeY) {
                transform.position = Vector3(position.x, safeY, position.z)
            }

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
}
