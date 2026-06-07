package com.example.matefairy01.interaction

import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.CollisionComponent
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
            picked.components[PhysicsVelocityComponent::class.java]?.linearVelocity = Vector3.ZERO

            val worldOffset = rotateByYaw(follow.localOffset, holderTransform.eulerAngles.yaw)
            pickedTransform.position = Vector3(
                holderTransform.position.x + worldOffset.x,
                holderTransform.position.y + worldOffset.y,
                holderTransform.position.z + worldOffset.z
            )
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
}
