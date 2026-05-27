package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpMaterialAPI {
    @Test
    fun dump() {
        val out = File("dump_material.txt")
        out.writeText("")
        try {
            val clazz = Class.forName("com.pico.spatial.core.ecs.ModelComponent")
            out.appendText("ModelComponent methods:\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${it.name}(${it.parameterTypes.joinToString { it.simpleName }}) -> ${it.returnType.simpleName}\n") 
            }
        } catch (e: Exception) {}
        
        val materials = listOf(
            "com.pico.spatial.core.ecs.resource.Material",
            "com.pico.spatial.core.ecs.resource.PhysicallyBasedMaterial",
            "com.pico.spatial.core.ecs.resource.UnlitMaterial"
        )
        materials.forEach { name ->
            try {
                val clazz = Class.forName(name)
                out.appendText("\n$name methods:\n")
                clazz.declaredMethods.forEach { 
                    if (it.name.contains("Depth", ignoreCase = true) || it.name.contains("Test", ignoreCase = true)) {
                        out.appendText("${it.name}(${it.parameterTypes.joinToString { it.simpleName }}) -> ${it.returnType.simpleName}\n")
                    }
                }
            } catch (e: Exception) {}
        }
    }
}
