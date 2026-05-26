package com.example.matefairy01.orchestrator.decision

/**
 * 默认的行为决策器实现
 * 
 * 优先级规则：
 * 1. 默认情况下，动作意图优先（如果动作包含特殊动画，如走过来，可能覆盖普通情绪）。
 * 2. 负面/强情绪优先级最高：如 "angry" (生气), "sad" (伤心)。
 *    当遇到这些高优情绪时，精灵会拒绝执行指令动作（拦截动作），仅表现情绪。
 */
class DefaultBehaviorDecisionMaker : BehaviorDecisionMaker {

    // 预设的高优先级情绪列表
    private val highPriorityEmotions = setOf("angry", "sad")

    override fun decide(
        emotion: String,
        actionIntent: String,
        originalReply: String
    ): BehaviorDecision {
        val normalizedEmotion = emotion.lowercase()
        val normalizedAction = actionIntent.lowercase()

        // 1. 如果没有实质性动作意图，正常表现情绪即可
        if (normalizedAction == "none" || normalizedAction.isEmpty()) {
            return BehaviorDecision(
                shouldTriggerEmotion = true,
                shouldDispatchAction = false,
                resolvedEmotion = normalizedEmotion,
                resolvedActionIntent = "none"
            )
        }

        // 2. 如果存在实质性动作意图，且当前情绪是高优负面情绪 -> 拦截动作，表现情绪
        if (highPriorityEmotions.contains(normalizedEmotion)) {
            return BehaviorDecision(
                shouldTriggerEmotion = true,
                shouldDispatchAction = false, // 拦截动作
                resolvedEmotion = normalizedEmotion,
                resolvedActionIntent = "none"
            )
        }

        // 3. 正常情况：动作优先或两者并行（比如一边开心一边飞过来）
        // 对于普通情绪（happy, neutral, shy 等），允许动作并行或覆盖
        return BehaviorDecision(
            shouldTriggerEmotion = true,
            shouldDispatchAction = true,
            resolvedEmotion = normalizedEmotion,
            resolvedActionIntent = normalizedAction
        )
    }
}
