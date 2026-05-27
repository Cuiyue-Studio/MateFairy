package com.example.test

import com.pico.spatial.ui.platform.containers.SpatialNavigator
import java.lang.reflect.Modifier

fun main() {
    val methods = SpatialNavigator::class.java.methods
    for (m in methods) {
        println("${m.name}")
    }
}
