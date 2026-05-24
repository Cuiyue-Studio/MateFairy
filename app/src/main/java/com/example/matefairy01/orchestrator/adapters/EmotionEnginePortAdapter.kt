package com.example.matefairy01.orchestrator.adapters

import com.example.matefairy01.emotion.EmotionEngine
import com.example.matefairy01.orchestrator.ports.EmotionCommandPort

class EmotionEnginePortAdapter(
    private val emotionEngine: EmotionEngine
) : EmotionCommandPort {
    override fun triggerEmotion(emotion: String) {
        emotionEngine.triggerEmotion(emotion)
    }
}
