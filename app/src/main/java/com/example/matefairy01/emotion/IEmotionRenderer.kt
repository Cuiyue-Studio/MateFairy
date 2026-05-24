package com.example.matefairy01.emotion

/**
 * 情绪渲染器接口，用于将 LLM 返回的 emotion 字段渲染为视觉表现（UI或动画）
 */
interface IEmotionRenderer {
    /**
     * 渲染指定的情绪
     * @param emotion 情绪类型（如 "happy", "sad", "neutral" 等）
     */
    fun renderEmotion(emotion: String)
}
