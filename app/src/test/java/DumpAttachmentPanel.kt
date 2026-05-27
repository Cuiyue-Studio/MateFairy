package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpAttachmentPanel {
    @Test
    fun dump() {
        val f = File("attachment_panel_dump.txt")
        try {
            val clz = Class.forName("com.pico.spatial.scene.ext.AttachmentPanelKt")
            clz.methods.forEach { 
                f.appendText("${it.name}(${it.parameterTypes.joinToString { p -> p.simpleName }})\n") 
            }
        } catch(e: Exception) {
            f.appendText("not found\n")
        }
    }
}
