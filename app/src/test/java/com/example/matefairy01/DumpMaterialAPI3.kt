package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpMaterialAPI3 {
    @Test
    fun dump() {
        val out = File("dump_material3.txt")
        out.writeText("")
        val classes = listOf(
            "com.pico.spatial.core.ecs.resources.Material",
            "com.pico.spatial.core.ecs.resources.UnlitMaterial",
            "com.pico.spatial.core.ecs.resources.PhysicallyBasedMaterial"
        )
        classes.forEach { name ->
            try {
                val clazz = Class.forName(name)
                out.appendText("\n--- $name ---\n")
                clazz.declaredMethods.forEach { 
                    out.appendText("${it.name}(${it.parameterTypes.joinToString { it.simpleName }}) -> ${it.returnType.simpleName}\n") 
                }
            } catch (e: Exception) {}
        }
    }
}
