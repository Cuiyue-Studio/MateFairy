package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpAttachmentPanelComponent {
    @Test
    fun dump() {
        val out = File("dump_apc.txt")
        out.writeText("")
        try {
            val clazz = Class.forName("com.pico.spatial.core.ecs.AttachmentPanelComponent")
            out.appendText("Found: ${clazz.name}\n")
            clazz.declaredMethods.forEach { 
                out.appendText("${Modifier.toString(it.modifiers)} ${it.returnType.simpleName} ${it.name}(${it.parameterTypes.joinToString { it.simpleName }})\n") 
            }
            clazz.declaredFields.forEach {
                out.appendText("${Modifier.toString(it.modifiers)} ${it.type.simpleName} ${it.name}\n")
            }
        } catch (e: Exception) {
            out.appendText(e.toString() + "\n")
        }
    }
}
