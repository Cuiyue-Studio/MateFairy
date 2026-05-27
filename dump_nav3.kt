package com.example.test

import com.pico.spatial.ui.platform.containers.SpatialNavigator

fun main() {
    val methods = SpatialNavigator::class.java.methods
    for (m in methods) {
        if(m.name.contains("Window")) println(m.name)
    }
}
