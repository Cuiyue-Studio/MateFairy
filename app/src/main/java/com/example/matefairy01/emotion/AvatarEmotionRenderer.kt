package com.example.matefairy01.emotion

import android.util.Log
import com.example.matefairy01.animation.AnimationConfig
import com.example.matefairy01.animation.AnimationController

/**
 * 将情绪意图映射到对应的动画配置上
 */
class AvatarEmotionRenderer(
    private val animationController: AnimationController
) : IEmotionRenderer {

    override fun renderEmotion(emotion: String) {
        Log.d("AvatarEmotionRenderer", "AI触发情绪: $emotion")
        
        val targetAnimation = AnimationConfig.getAnimationByEmotion(emotion)
        if (targetAnimation != null) {
            animationController.playAnimation(targetAnimation)
        } else {
            Log.d("AvatarEmotionRenderer", "情绪 [$emotion] 无特殊动画绑定，保持原状")
        }
    }
}
