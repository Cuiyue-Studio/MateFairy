package com.example.matefairy01.emotion

import android.util.Log

/**
 * 情绪引擎，用于统一管理和控制情绪的频控、渲染及过渡
 */
class EmotionEngine(
    private val renderer: IEmotionRenderer
) {
    private var currentEmotion: String = "neutral"

    /**
     * 触发情绪变更
     */
    fun triggerEmotion(emotion: String) {
        if (emotion.isEmpty()) {
            return
        }

        Log.d("EmotionEngine", "Transitioning emotion from $currentEmotion to $emotion")
        currentEmotion = emotion

        // 情绪动画是对“当前这句话”的 one-shot 反应，相同情绪也必须允许重复播放。
        renderer.renderEmotion(emotion)
    }

    /**
     * 重置为默认情绪
     */
    fun resetEmotion() {
        triggerEmotion("neutral")
    }
}
