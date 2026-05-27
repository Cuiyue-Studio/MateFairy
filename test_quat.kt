package com.example.test

import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3

fun main() {
    val q = Quat.identity()
    val v = Vector3(0f, 0f, -1f)
    val result = q * v
    println("Success")
}
