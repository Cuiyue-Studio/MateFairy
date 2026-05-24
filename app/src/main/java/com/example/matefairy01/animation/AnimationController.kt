package com.example.matefairy01.animation

import com.pico.spatial.core.ecs.Entity

/**
 * 为行为系统和场景层提供稳定的动画控制接口。
 */
interface AnimationController {
    fun initialize(robotEntity: Entity)

    fun playAnimation(animation: FairyAnimation)

    fun playRandomIdleAnimation(): FairyAnimation?

    fun playRandomMovingAnimation(): FairyAnimation?

    fun cleanup()
}
