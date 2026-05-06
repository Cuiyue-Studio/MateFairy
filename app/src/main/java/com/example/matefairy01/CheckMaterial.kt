package com.example.matefairy01

import com.pico.spatial.core.ecs.resource.BlendingMode
import com.pico.spatial.core.ecs.resource.UnlitMaterial
import com.pico.spatial.core.ecs.resource.MeshResource
import com.pico.spatial.core.ecs.ModelComponent
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.math.Color4

fun check() {
    val material = UnlitMaterial.create(BlendingMode.OPAQUE)
    material.setBaseColor(Color4(0f, 0f, 0f, 0f))
    // material.blendingMode = BlendingMode.Transparent?
}
