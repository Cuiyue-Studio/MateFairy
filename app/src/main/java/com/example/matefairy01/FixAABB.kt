package com.example.matefairy01

import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.BoundingBox
import com.pico.spatial.core.math.Vector3

fun expandEntityAABB(entity: Entity) {
    val t = entity.components[TransformComponent::class.java]
    // AABB expansion in SDK is not exposed directly.
    // Try to use a transparent large ModelComponent?
}
