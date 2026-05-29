package com.example.matefairy01.ai

import kotlinx.coroutines.delay

/**
 * Mock LLM 提供者
 * 用于开发和测试阶段，模拟 AI 回复
 */
class MockLLMProvider : ILLMProvider {

    override var systemPrompt: String = "You are a helpful fairy companion."

    override suspend fun chat(messages: List<ChatMessage>): AIResponse {
        // 模拟网络延迟
        delay(1000)

        // 获取最后一条用户消息
        val lastUserMessage = messages.findLast { it.role == "user" }?.content ?: ""

        // 根据用户输入生成简单的模拟回复
        val (replyText, emotion, actionIntent) = when {
            lastUserMessage.contains("hello", ignoreCase = true) ||
            lastUserMessage.contains("hi", ignoreCase = true) ||
            lastUserMessage.contains("你好", ignoreCase = true) -> {
                Triple("你好呀！我是你的精灵伙伴，很高兴见到你！", "happy", "wave")
            }
            lastUserMessage.contains("dance", ignoreCase = true) ||
            lastUserMessage.contains("跳舞", ignoreCase = true) -> {
                Triple("好的，让我为你跳一支舞吧！", "happy", "dance")
            }
            lastUserMessage.contains("come", ignoreCase = true) ||
            lastUserMessage.contains("过来", ignoreCase = true) -> {
                Triple("我来了！", "neutral", "come_to_player")
            }
            lastUserMessage.contains("sad", ignoreCase = true) ||
            lastUserMessage.contains("难过", ignoreCase = true) -> {
                Triple("别难过，我会一直陪着你的。", "sad", "comfort")
            }
            else -> {
                Triple("我明白了，这是一个很有趣的话题！让我想想...", "neutral", "none")
            }
        }

        return AIResponse(
            reply_text = replyText,
            emotion = emotion,
            action_intent = actionIntent
        )
    }

    override suspend fun summarize(messages: List<ChatMessage>): String {
        delay(500)
        return "Summary of previous conversation about various topics."
    }

    override suspend fun complete(
        systemPrompt: String,
        userMessage: String,
        maxTokens: Int?,
        temperature: Double?
    ): String {
        delay(200)
        // Mock 模式：返回一个不会触发抽取也不引发矛盾检测的安全回复
        return "{\"facts\": []}"
    }
}
