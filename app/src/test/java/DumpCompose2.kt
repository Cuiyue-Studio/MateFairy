package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.lang.reflect.Modifier

class DumpCompose2 {
    @Test
    fun dump() {
        val f = File("compose2.txt")
        val pkgs = listOf("com.pico.spatial.ui.foundation.DrawOrderGroupComponentKt", "com.pico.spatial.scene.ext.DrawOrderGroupKt", "com.pico.spatial.ui.foundation.OverlayKt")
        for (pkg in pkgs) {
            try {
                val clz = Class.forName(pkg)
                f.appendText("$pkg\n")
            } catch(e: Exception) {}
        }
    }
}
