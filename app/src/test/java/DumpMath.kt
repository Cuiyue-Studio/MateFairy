package com.example.matefairy01

import org.junit.Test
import com.pico.spatial.core.math.Quat
import com.pico.spatial.core.math.Vector3
import java.lang.reflect.Modifier
import java.io.File

class DumpMath {
    @Test
    fun dump() {
        val f = File("math_dump.txt")
        f.writeText("--- Quat methods ---\n")
        Quat::class.java.methods.forEach {
            f.appendText("${Modifier.toString(it.modifiers)} ${it.returnType.simpleName} ${it.name}(${it.parameterTypes.joinToString { p -> p.simpleName }})\n")
        }
        f.appendText("--- Vector3 methods ---\n")
        Vector3::class.java.methods.forEach {
            f.appendText("${Modifier.toString(it.modifiers)} ${it.returnType.simpleName} ${it.name}(${it.parameterTypes.joinToString { p -> p.simpleName }})\n")
        }
    }
}
