package com.example.matefairy01.orchestrator

import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.memory.ContextMemorySystem
import com.example.matefairy01.orchestrator.ports.ActionCommandPort
import com.example.matefairy01.orchestrator.ports.EmotionCommandPort
data class ConversationResult(
    val replyText: String
)

/**
 * 统一封装“用户输入 -> 上下文 -> LLM -> 情绪/动作分发”的编排逻辑。
 *
 * 第一阶段只做逻辑搬迁，不改变现有协议和执行路径，确保功能行为保持一致。
 */
class ConversationOrchestrator(
    private val llmProvider: ILLMProvider,
    private val contextMemorySystem: ContextMemorySystem,
    private val emotionPort: EmotionCommandPort,
    private val actionPort: ActionCommandPort
) {
    suspend fun processUserInput(text: String): ConversationResult {
        contextMemorySystem.addMessage(ChatMessage(role = "user", content = text))

        val messages = contextMemorySystem.buildPromptMessages()
        val response = llmProvider.chat(messages)

        contextMemorySystem.addMessage(
            ChatMessage(role = "assistant", content = response.reply_text)
        )

        emotionPort.triggerEmotion(response.emotion)
        actionPort.dispatchAction(response.action_intent)

        return ConversationResult(replyText = response.reply_text)
    }
}
