package com.example.matefairy01.ai

/**
 * AI返回的结果结构
 */
data class AIResponse(
    val status: String = "ok",
    val reply_text: String,
    val emotion: String = "neutral",
    val action_intent: String = "none"
)
