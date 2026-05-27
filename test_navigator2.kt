package com.example.test

import com.pico.spatial.ui.platform.containers.SpatialNavigator

fun main() {
    val methods = SpatialNavigator::class.java.methods
    for (method in methods) {
        if (method.name.contains("Window", ignoreCase = true) || method.name.contains("Pose", ignoreCase = true)) {
            println(method.name)
        }
    }
}
