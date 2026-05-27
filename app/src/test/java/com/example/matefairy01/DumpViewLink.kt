package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpViewLink {
    @Test
    fun dump() {
        val out = File("dump_viewlink.txt")
        out.writeText("")
        try {
            val clazz = Class.forName("android.view.ViewLink")
            out.appendText("ViewLink methods:\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${it.name}(${it.parameterTypes.joinToString { it.simpleName }}) -> ${it.returnType.simpleName}\n") 
            }
        } catch (e: Exception) { out.appendText("Error: $e\n") }
    }
}
