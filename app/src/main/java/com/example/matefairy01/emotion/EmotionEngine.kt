package com.example.matefairy01.emotion

import android.util.Log
import com.pico.spatial.core.ecs.Entity

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
    fun triggerEmotion(emotion: String, fairyEntity: Entity) {
        if (emotion.isEmpty() || emotion == currentEmotion) {
            return // 避免重复触发相同情绪
        }
        
        Log.d("EmotionEngine", "Transitioning emotion from $currentEmotion to $emotion")
        currentEmotion = emotion
        
        // 调用渲染器进行实际表现（如播放 BlendShape 动画或更新 UI）
        renderer.renderEmotion(emotion, fairyEntity)
    }

    /**
     * 重置为默认情绪
     */
    fun resetEmotion(fairyEntity: Entity) {
        triggerEmotion("neutral", fairyEntity)
    }
}
