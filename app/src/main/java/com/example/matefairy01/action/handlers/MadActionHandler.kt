package com.example.matefairy01.action.handlers

import com.example.matefairy01.action.IActionHandler
import com.example.matefairy01.animation.AnimationController
import com.example.matefairy01.animation.FairyAnimation

class MadActionHandler(
    private val animationController: AnimationController
) : IActionHandler {
    // 对应大模型输出的 JSON 意图
    override val intent: String = "mad"

    override suspend fun execute(params: Map<String, Any>) {
        animationController.playAnimation(FairyAnimation.MAD_ACTION)
    }
}
