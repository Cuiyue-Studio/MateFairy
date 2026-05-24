package com.example.matefairy01.avatar

import com.example.matefairy01.animation.FairyAnimation
import com.pico.spatial.core.ecs.Entity

/**
 * 统一封装精灵形象相关的运行时控制能力。
 *
 * 当前先收口动画初始化与行为系统发起的动画请求，
 * 后续可继续扩展为动作、表情、状态仲裁的统一入口。
 */
interface AvatarController {
    fun initialize(robotEntity: Entity)

    fun playSpawnAnimation()

    fun requestMovingAnimation()

    fun requestIdleAnimation(): FairyAnimation?

    fun cleanup()
}
