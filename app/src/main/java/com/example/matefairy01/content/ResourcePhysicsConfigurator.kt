package com.example.matefairy01.content

import com.pico.spatial.core.ecs.CollisionComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.HoverEffectComponent
import com.pico.spatial.core.ecs.InteractableComponent
import com.pico.spatial.core.ecs.PhysicsForceComponent
import com.pico.spatial.core.ecs.RigidBodyComponent
import com.pico.spatial.core.ecs.resource.PhysicsMaterialResource
import com.pico.spatial.core.ecs.resource.ShapeResource
import com.pico.spatial.core.ecs.simulation.CollisionDetectionMode
import com.pico.spatial.core.ecs.simulation.CollisionFilter
import com.pico.spatial.core.ecs.simulation.CollisionInfoDetailLevel
import com.pico.spatial.core.ecs.simulation.CollisionResponseMode
import com.pico.spatial.core.ecs.simulation.RigidBodyMode
import com.pico.spatial.core.math.Vector3

object ResourcePhysicsConfigurator {
    fun configureFootball(football: Entity) {
        configureDynamicBall(
            ball = football,
            config = BallPhysicsConfig(
                colliderRadius = 0.11f,
                staticFriction = 0.8f,
                dynamicFriction = 0.8f,
                restitution = 0.85f,
                linearDamping = 0.2f,
                angularDamping = 0.2f
            )
        )
    }

    fun configureBasketball(basketball: Entity) {
        configureDynamicBall(
            ball = basketball,
            config = BallPhysicsConfig(
                colliderRadius = 0.12f,
                staticFriction = 0.7f,
                dynamicFriction = 0.7f,
                restitution = 0.9f,
                linearDamping = 0.18f,
                angularDamping = 0.18f
            )
        )
    }

    fun configureBoombox(boombox: Entity) {
        configureDynamicBox(
            entity = boombox,
            config = BoxPhysicsConfig(
                colliderSize = Vector3(0.33f, 0.22f, 0.1f),
                staticFriction = 0.9f,
                dynamicFriction = 0.8f,
                restitution = 0.35f,
                linearDamping = 0.35f,
                angularDamping = 0.45f
            )
        )
    }

    fun configureRubberDuck(duck: Entity) {
        configureDynamicBox(
            entity = duck,
            config = BoxPhysicsConfig(
                colliderSize = Vector3(0.2f, 0.26f, 0.28f),
                staticFriction = 0.75f,
                dynamicFriction = 0.65f,
                restitution = 0.55f,
                linearDamping = 0.25f,
                angularDamping = 0.3f
            )
        )
    }

    private fun configureDynamicBall(ball: Entity, config: BallPhysicsConfig) {
        configureDynamicRigidBody(
            entity = ball,
            shapes = listOf(ShapeResource.createSphere(config.colliderRadius)),
            material = PhysicsMaterialResource(
                staticFriction = config.staticFriction,
                dynamicFriction = config.dynamicFriction,
                restitution = config.restitution
            ),
            linearDamping = config.linearDamping,
            angularDamping = config.angularDamping,
            floorOffset = config.colliderRadius
        )
    }

    private fun configureDynamicBox(entity: Entity, config: BoxPhysicsConfig) {
        configureDynamicRigidBody(
            entity = entity,
            shapes = listOf(ShapeResource.createBox(config.colliderSize)),
            material = PhysicsMaterialResource(
                staticFriction = config.staticFriction,
                dynamicFriction = config.dynamicFriction,
                restitution = config.restitution
            ),
            linearDamping = config.linearDamping,
            angularDamping = config.angularDamping,
            floorOffset = config.colliderSize.y / 2f
        )
    }

    private fun configureDynamicRigidBody(
        entity: Entity,
        shapes: List<ShapeResource>,
        material: PhysicsMaterialResource,
        linearDamping: Float,
        angularDamping: Float,
        floorOffset: Float
    ) {
        entity.components.set(
            CollisionComponent(
                collisionShape = shapes,
                physicsMaterial = material,
                collisionResponseMode = CollisionResponseMode.COLLIDER_FULL,
                collisionFilter = CollisionFilter.COLLISION_FILTER_DEFAULT,
                collisionInfoDetailLevel = CollisionInfoDetailLevel.BRIEF
            )
        )

        val rigidBody = entity.components[RigidBodyComponent::class.java] ?: RigidBodyComponent()
        rigidBody.apply {
            rigidBodyMode = RigidBodyMode.DYNAMIC
            isAffectedByGravity = false
            collisionDetectionMode = CollisionDetectionMode.CONTINUOUS
            this.linearDamping = linearDamping
            this.angularDamping = angularDamping
        }
        entity.components.set(rigidBody)

        if (entity.components[PhysicsForceComponent::class.java] == null) {
            entity.components.set(PhysicsForceComponent())
        }
        if (entity.components[InteractableComponent::class.java] == null) {
            entity.components.set(InteractableComponent())
        }
        if (entity.components[HoverEffectComponent::class.java] == null) {
            entity.components.set(HoverEffectComponent())
        }

        entity.components.set(
            ResourcePhysicsActivationComponent(floorOffset = floorOffset)
        )
    }
}

private data class BallPhysicsConfig(
    val colliderRadius: Float,
    val staticFriction: Float,
    val dynamicFriction: Float,
    val restitution: Float,
    val linearDamping: Float,
    val angularDamping: Float
)

private data class BoxPhysicsConfig(
    val colliderSize: Vector3,
    val staticFriction: Float,
    val dynamicFriction: Float,
    val restitution: Float,
    val linearDamping: Float,
    val angularDamping: Float
)
