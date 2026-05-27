package com.example.test

import com.pico.spatial.ui.platform.containers.SpatialNavigator
import java.io.File

fun main() {
    val methods = SpatialNavigator::class.java.methods
    val out = File("methods.txt")
    out.writeText(methods.joinToString("\n") { it.name })
}
