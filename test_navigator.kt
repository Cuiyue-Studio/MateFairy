package com.example.test
import com.pico.spatial.ui.platform.containers.SpatialNavigator

fun main() {
    val methods = SpatialNavigator::class.java.declaredMethods
    methods.forEach { println(it.name) }
}
