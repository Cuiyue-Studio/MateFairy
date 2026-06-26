package com.example.matefairy01.behavior

import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.ecs.simulation.RigidBodyMode
import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.sense.base.SemanticLabelType
import java.util.UUID

/**
 * Persistent state for a fairy staying on a real-world semantic object.
 * It blocks autonomous behavior without holding the short-lived interaction action lock.
 */
class FairySemanticResidenceComponent(
    val residenceId: String,
    val semantic: SemanticLabelType,
    val anchorUUID: UUID,
    val targetPosition: Vector3,
    val targetRotation: Quat,
    val enteredAtMillis: Long = System.currentTimeMillis(),
    val previousCollisionResponseMode: CollisionResponseMode? = null,
    var idleRefreshSeconds: Float = 0f
) : Component()

object FairySemanticResidenceRuntime {
    private var activeEntity: Entity? = null

    fun enter(entity: Entity, component: FairySemanticResidenceComponent) {
        entity.components.set(component)
        activeEntity = entity
    }

    fun clear(reason: String = "unspecified"): Boolean {
        val entity = activeEntity ?: return false
        val component = entity.components[FairySemanticResidenceComponent::class.java] ?: return false
        entity.components.remove(FairySemanticResidenceComponent::class.java)
        restoreResidencePhysics(entity, component)
        activeEntity = null
        return true
    }

    fun clearIfEntity(entity: Entity): Boolean {
        if (activeEntity != entity) return false
        return clear("entity")
    }

    fun isActive(): Boolean {
        return activeEntity?.components?.get(FairySemanticResidenceComponent::class.java) != null
    }

    private fun restoreResidencePhysics(entity: Entity, component: FairySemanticResidenceComponent) {
        entity.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
        entity.components[PhysicsVelocityComponent::class.java]?.let { velocity ->
            velocity.linearVelocity = Vector3.ZERO
            velocity.angularVelocity = Vector3.ZERO
        }
        entity.components[RigidBodyComponent::class.java]?.let { rigidBody ->
            rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
            rigidBody.isAffectedByGravity = false
        }
        entity.components[CollisionComponent::class.java]?.collisionResponseMode =
            CollisionResponseMode.TRIGGER_LITE
    }
}
