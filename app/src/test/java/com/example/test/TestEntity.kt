package com.example.test

import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.Component
import org.junit.Test

class TestEntity {
    @Test
    fun test() {
        val methods = Entity::class.java.methods.map { it.name }.distinct().sorted().joinToString("\n")
        java.io.File("/Users/bytedance/AndroidStudioProjects/MateFairy01/entity_methods.txt").writeText("Entity:\n$methods")
    }
}