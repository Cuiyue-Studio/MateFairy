package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpBackgroundMaterial {
    @Test
    fun dump() {
        val out = File("dump_bg_material.txt")
        out.writeText("")
        try {
            val clazz = Class.forName("com.pico.spatial.ui.foundation.design.MaterialModifierKt")
            out.appendText("Found MaterialModifierKt\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${Modifier.toString(it.modifiers)} ${it.returnType.simpleName} ${it.name}(${it.parameterTypes.joinToString { it.simpleName }})\n") 
            }
        } catch (e: Exception) {
            out.appendText("MaterialModifierKt error: $e\n")
        }
        
        try {
            val clazz = Class.forName("com.pico.spatial.ecs.resources.Material")
            out.appendText("\nFound Material\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${Modifier.toString(it.modifiers)} ${it.returnType.simpleName} ${it.name}(${it.parameterTypes.joinToString { it.simpleName }})\n") 
            }
        } catch (e: Exception) {
            out.appendText("Material error: $e\n")
        }
    }
}
