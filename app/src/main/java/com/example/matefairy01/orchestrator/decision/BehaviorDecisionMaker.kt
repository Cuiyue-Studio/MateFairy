package com.example.matefairy01.orchestrator.decision

/**
 * 行为决策结果
 */
data class BehaviorDecision(
    val shouldTriggerEmotion: Boolean,
    val shouldDispatchAction: Boolean,
    val resolvedEmotion: String,
    val resolvedActionIntent: String,
    val overriddenReplyText: String? = null // 如果因为情绪原因拦截了动作，可以考虑稍微修改回复，目前可选
)

/**
 * 行为决策器接口
 * 负责解决同一时刻下发的情绪意图和动作意图之间的冲突。
 */
interface BehaviorDecisionMaker {
    fun decide(emotion: String, actionIntent: String, originalReply: String): BehaviorDecision
}
