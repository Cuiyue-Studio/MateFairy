package com.example.matefairy01.orchestrator.ports

/**
 * 对话编排层使用的情绪分发端口。
 *
 * 场景实体的查找与绑定应由端口适配器内部负责，编排层只表达“触发情绪”这一意图。
 */
interface EmotionCommandPort {
    fun triggerEmotion(emotion: String)
}
