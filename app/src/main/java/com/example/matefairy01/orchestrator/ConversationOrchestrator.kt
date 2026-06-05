package com.example.matefairy01.orchestrator

import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.memory.ContextMemorySystem
import com.example.matefairy01.orchestrator.decision.BehaviorDecisionMaker
import com.example.matefairy01.orchestrator.decision.DefaultBehaviorDecisionMaker
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
    private val actionPort: ActionCommandPort,
    private val decisionMaker: BehaviorDecisionMaker = DefaultBehaviorDecisionMaker()
) {
    suspend fun processUserInput(text: String): ConversationResult {
        contextMemorySystem.addMessage(ChatMessage(role = "user", content = text))

        val messages = contextMemorySystem.buildPromptMessages()
        val response = llmProvider.chat(messages)

        contextMemorySystem.addMessage(
            ChatMessage(role = "assistant", content = response.reply_text)
        )

        // 3. 决策优先级拦截 (动作 vs 情绪)
        val decision = decisionMaker.decide(
            emotion = response.emotion,
            actionIntent = response.action_intent,
            originalReply = response.reply_text
        )

        // 4. 分发执行
        if (decision.shouldTriggerEmotion) {
            // 情绪分发是同步非阻塞的，直接调用
            emotionPort.triggerEmotion(decision.resolvedEmotion)
        }

        val actionReplyOverride = if (decision.shouldDispatchAction) {
            actionPort.dispatchAction(decision.resolvedActionIntent).replyOverride
        } else {
            null
        }

        return ConversationResult(
            replyText = actionReplyOverride ?: decision.overriddenReplyText ?: response.reply_text
        )
    }
}
