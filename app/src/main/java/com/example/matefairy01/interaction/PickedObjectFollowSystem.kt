package com.example.matefairy01.interaction

import com.example.matefairy01.playerinteraction.PlayerFairyInteractionComponent
import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.EntityQueryCondition
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.SceneUpdateContext
import com.pico.spatial.core.ecs.System
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.ecs.simulation.RigidBodyMode
import com.pico.spatial.core.math.Vector3
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class PickedObjectFollowComponent(
    val holderActorId: String = DEFAULT_FAIRY_ACTOR_ID,
    val localOffset: Vector3 = Vector3(0f, -0.05f, 0.28f)
) : Component() {
    var previousCollisionResponseMode: CollisionResponseMode? = null
}

class PickedObjectFollowSystem : System() {
    private val pickedCondition =
        EntityQueryCondition.hasComponent(PickedObjectFollowComponent::class.java)

    override fun update(context: SceneUpdateContext) {
        context.scene.queryEntity(pickedCondition).forEach { picked ->
            val follow = picked.components[PickedObjectFollowComponent::class.java] ?: return@forEach
            val holder = InteractionEntityResolver.findActor(context.scene, follow.holderActorId) ?: return@forEach
            if (holder.components[PlayerFairyInteractionComponent::class.java] != null) {
                releasePickedObject(picked, follow, holder)
                return@forEach
            }
            val holderTransform = holder.components[TransformComponent::class.java] ?: return@forEach
            val pickedTransform = picked.components[TransformComponent::class.java] ?: return@forEach

            picked.components[RigidBodyComponent::class.java]?.let { rigidBody ->
                rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
                rigidBody.isAffectedByGravity = false
            }
            picked.components[CollisionComponent::class.java]?.let { collision ->
                if (follow.previousCollisionResponseMode == null) {
                    follow.previousCollisionResponseMode = collision.collisionResponseMode
                }
                collision.collisionResponseMode = CollisionResponseMode.TRIGGER_LITE
            }
            picked.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
            picked.components[PhysicsVelocityComponent::class.java]?.let { velocity ->
                velocity.linearVelocity = Vector3.ZERO
                velocity.angularVelocity = Vector3.ZERO
            }

            val worldOffset = rotateByYaw(follow.localOffset, holderTransform.eulerAngles.yaw)
            pickedTransform.position = Vector3(
                holderTransform.position.x + worldOffset.x,
                holderTransform.position.y + worldOffset.y,
                holderTransform.position.z + worldOffset.z
            )
        }
    }

    private fun releasePickedObject(
        picked: Entity,
        follow: PickedObjectFollowComponent,
        holder: Entity
    ) {
        val holderTransform = holder.components[TransformComponent::class.java]
        val pickedTransform = picked.components[TransformComponent::class.java]
        if (holderTransform != null && pickedTransform != null) {
            val dropOffset = rotateByYaw(SAFE_DROP_LOCAL_OFFSET, holderTransform.eulerAngles.yaw)
            pickedTransform.position = Vector3(
                holderTransform.position.x + dropOffset.x,
                holderTransform.position.y + dropOffset.y,
                holderTransform.position.z + dropOffset.z
            )
        }
        picked.components.remove(PickedObjectFollowComponent::class.java)
        picked.components[CollisionComponent::class.java]?.collisionResponseMode =
            follow.previousCollisionResponseMode ?: CollisionResponseMode.COLLIDER_FULL
        picked.components[RigidBodyComponent::class.java]?.let { rigidBody ->
            rigidBody.rigidBodyMode = RigidBodyMode.DYNAMIC
            rigidBody.isAffectedByGravity = true
        }
        picked.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
        picked.components[PhysicsVelocityComponent::class.java]?.let { velocity ->
            velocity.linearVelocity = Vector3.ZERO
            velocity.angularVelocity = Vector3.ZERO
        }
    }

    private fun rotateByYaw(offset: Vector3, yawDegrees: Float): Vector3 {
        val radians = yawDegrees * PI.toFloat() / 180f
        val cosValue = cos(radians)
        val sinValue = sin(radians)
        return Vector3(
            offset.x * cosValue - offset.z * sinValue,
            offset.y,
            offset.x * sinValue + offset.z * cosValue
        )
    }

    private companion object {
        val SAFE_DROP_LOCAL_OFFSET = Vector3(0f, -0.18f, 0.5f)
    }
}
