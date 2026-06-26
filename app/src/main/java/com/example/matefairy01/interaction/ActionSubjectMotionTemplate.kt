package com.example.matefairy01.interaction

import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Component
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.PhysicsVelocityComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.ecs.simulation.RigidBodyMode
import com.pico.spatial.core.math.Vector3

/**
 * Shared template for actions that drive the fairy by directly writing Transform.
 * The subject should not produce physical impulses while it is script-controlled.
 */
class ActionSubjectMotionTemplate {
    fun prepare(subject: Entity) {
        subject.components[RigidBodyComponent::class.java]?.let { rigidBody ->
            rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
            rigidBody.isAffectedByGravity = false
        }
        subject.components[CollisionComponent::class.java]?.let { collision ->
            collision.collisionResponseMode = CollisionResponseMode.TRIGGER_LITE
        }
        clearMotion(subject)
    }

    fun restore(subject: Entity) {
        clearMotion(subject)
        subject.components[RigidBodyComponent::class.java]?.let { rigidBody ->
            rigidBody.rigidBodyMode = RigidBodyMode.KINEMATIC
            rigidBody.isAffectedByGravity = false
        }
        subject.components[CollisionComponent::class.java]?.collisionResponseMode =
            CollisionResponseMode.TRIGGER_LITE
        subject.components.set(ActionRecoveryGraceComponent())
    }

    private fun clearMotion(subject: Entity) {
        subject.components[PhysicsForceComponent::class.java]?.force = Vector3.ZERO
        val velocity = subject.components[PhysicsVelocityComponent::class.java]
            ?: PhysicsVelocityComponent().also { subject.components.set(it) }
        velocity.linearVelocity = Vector3.ZERO
        velocity.angularVelocity = Vector3.ZERO
    }
}

class ActionRecoveryGraceComponent(
    var remainingSeconds: Float = DEFAULT_DURATION_SECONDS,
    var hasResetBehaviorState: Boolean = false,
    var anchorPosition: Vector3? = null
) : Component() {
    companion object {
        const val DEFAULT_DURATION_SECONDS = 0.8f
    }
}
