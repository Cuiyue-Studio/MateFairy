package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpDrawOrderGroup {
    @Test
    fun dump() {
        val out = File("dump_dog.txt")
        out.writeText("")
        try {
            val clazz = Class.forName("com.pico.spatial.core.ecs.DrawOrderGroup")
            out.appendText("DrawOrderGroup methods:\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${it.name}(${it.parameterTypes.joinToString { it.simpleName }}) -> ${it.returnType.simpleName}\n") 
            }
        } catch (e: Exception) { out.appendText("Error: $e\n") }
        
        try {
            val clazz = Class.forName("com.pico.spatial.core.ecs.DrawOrderGroupComponent")
            out.appendText("\nDrawOrderGroupComponent methods:\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${it.name}(${it.parameterTypes.joinToString { it.simpleName }}) -> ${it.returnType.simpleName}\n") 
            }
        } catch (e: Exception) { out.appendText("Error: $e\n") }
    }
}
