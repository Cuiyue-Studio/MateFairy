package com.example.matefairy01

import org.junit.Test
import java.io.File
import java.net.URLClassLoader

class DumpDrawOrderAPI {
    @Test
    fun dump() {
        val out = File("dump_draw_order.txt")
        out.writeText("")
        val prefixes = listOf(
            "com.pico.spatial.scene.DrawOrderGroupComponent",
            "com.pico.spatial.ecs.components.DrawOrderGroupComponent",
            "com.pico.spatial.component.DrawOrderGroupComponent",
            "com.pico.spatial.core.scene.components.DrawOrderGroupComponent",
            "com.pico.spatial.engine.components.DrawOrderGroupComponent",
            "com.pico.spatial.scene.components.DrawOrderGroupComponent",
            "com.pico.spatial.DrawOrderGroupComponent"
        )
        for (pkg in prefixes) {
            try {
                val clazz = Class.forName(pkg)
                out.appendText("Found: ${clazz.name}\n")
            } catch (e: Exception) {
            }
        }
    }
}
