package com.example.matefairy01.ai

/**
 * 上下文消息
 */
data class ChatMessage(
    val role: String, // "system", "user", "assistant"
    val content: String
)
