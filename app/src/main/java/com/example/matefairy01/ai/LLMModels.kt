package com.example.matefairy01.ai

import kotlinx.serialization.Serializable

/**
 * AI返回的结果结构
 */
@Serializable
data class AIResponse(
    val reply_text: String,
    val emotion: String = "neutral",
    val action_intent: String = "none"
)

/**
 * 上下文消息
 */
@Serializable
data class ChatMessage(
    val role: String, // "system", "user", "assistant"
    val content: String
)
