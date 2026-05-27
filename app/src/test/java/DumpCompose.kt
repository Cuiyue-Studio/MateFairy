package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpCompose {
    @Test
    fun dump() {
        val f = File("compose_dump.txt")
        try {
            val clz = Class.forName("com.pico.spatial.scene.ext.AttachmentPanelKt")
            clz.methods.forEach { f.appendText("${it.name}\n") }
        } catch(e: Exception) {}
        try {
            val clz = Class.forName("com.pico.spatial.ui.foundation.PanelKt")
            clz.methods.forEach { f.appendText("${it.name}\n") }
        } catch(e: Exception) {}
    }
}
