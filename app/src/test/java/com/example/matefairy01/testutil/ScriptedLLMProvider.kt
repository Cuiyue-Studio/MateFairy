package com.example.matefairy01.testutil

import com.example.matefairy01.ai.AIResponse
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.ILLMProvider

/**
 * 可编排的假 LLM，仅供离线单测使用。
 *
 * 通过注入 lambda 决定每次调用的返回，从而让 FactExtractor / ContradictionChecker
 * 等依赖 LLM 输出的链路变得完全确定、可断言，且不依赖网络。
 */
class ScriptedLLMProvider(
    /** complete() 的响应器：可根据 systemPrompt / userMessage 返回不同 JSON */
    var completeResponder: (systemPrompt: String, userMessage: String) -> String = { _, _ -> "{}" },
    var chatResponder: (List<ChatMessage>) -> AIResponse = { AIResponse("好的", "neutral", "none") },
    var summarizeResponder: (List<ChatMessage>) -> String = { "对话摘要" }
) : ILLMProvider {

    override var systemPrompt: String = ""

    /** 记录每次 complete 的入参，便于断言调用发生 */
    val completeCalls = mutableListOf<Pair<String, String>>()

    override suspend fun chat(messages: List<ChatMessage>): AIResponse = chatResponder(messages)

    override suspend fun summarize(messages: List<ChatMessage>): String = summarizeResponder(messages)

    override suspend fun complete(
        systemPrompt: String,
        userMessage: String,
        maxTokens: Int?,
        temperature: Double?
    ): String {
        completeCalls += systemPrompt to userMessage
        return completeResponder(systemPrompt, userMessage)
    }
}
