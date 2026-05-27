package com.example.matefairy01.action.handlers

import com.example.matefairy01.action.IActionHandler
import com.example.matefairy01.animation.AnimationConfig
import com.example.matefairy01.animation.AnimationController

/**
 * 通用的非任务型动画处理器
 * 只要在 AnimationConfig 中配置了 NON_TASK_ACTION 类型的意图，
 * 就可以由该 Handler 统一接管，不再需要为每个动作手写 Handler。
 */
class GenericAnimationHandler(
    override val intent: String,
    private val animationController: AnimationController
) : IActionHandler {
    
    override suspend fun execute(params: Map<String, Any>) {
        val anim = AnimationConfig.getAnimationByAction(intent)
        if (anim != null) {
            animationController.playAnimation(anim)
        }
    }
}
