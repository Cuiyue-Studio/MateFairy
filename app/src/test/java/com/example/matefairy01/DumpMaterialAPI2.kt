package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpMaterialAPI2 {
    @Test
    fun dump() {
        val out = File("dump_material2.txt")
        out.writeText("")
        try {
            val clazz = Class.forName("com.pico.spatial.ecs.resources.Material")
            out.appendText("Material methods:\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${it.name}(${it.parameterTypes.joinToString { it.simpleName }}) -> ${it.returnType.simpleName}\n") 
            }
        } catch (e: Exception) {}
        
        try {
            val clazz = Class.forName("com.pico.spatial.core.ecs.resources.Material")
            out.appendText("Core Material methods:\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${it.name}(${it.parameterTypes.joinToString { it.simpleName }}) -> ${it.returnType.simpleName}\n") 
            }
        } catch (e: Exception) {}
    }
}
