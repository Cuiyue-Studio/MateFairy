package com.example.matefairy01.emotion

/**
 * 默认空实现，供重构阶段或未接入具体情绪表现时使用。
 */
class NoOpEmotionRenderer : IEmotionRenderer {
    override fun renderEmotion(emotion: String) {
        // Intentionally empty.
    }
}
