package com.example.matefairy01

import org.junit.Test
import kotlinx.coroutines.runBlocking
import com.example.matefairy01.ai.DeepSeekLLMProvider
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.config.DeepSeekConfig

class TestLLM {
    @Test
    fun testDeepSeek() = runBlocking {
        val provider = DeepSeekLLMProvider(DeepSeekConfig(), "你是一个陪伴型空间精灵。请用简洁、自然、友好的中文和玩家交流，回复尽量控制在 1 到 2 句话内。")
        try {
            val messages = listOf(
                ChatMessage("system", "你是一个陪伴型空间精灵。请用简洁、自然、友好的中文和玩家交流，回复尽量控制在 1 到 2 句话内。"),
                ChatMessage("user", "你好，精灵！")
            )
            val response = provider.chat(messages)
            println("=== RESPONSE START ===")
            println(response.reply_text)
            println("=== RESPONSE END ===")
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
