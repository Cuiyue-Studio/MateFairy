package com.example.matefairy01.playerinteraction

import com.example.matefairy01.animation.FairyAnimation

/**
 * 仅供玩家-精灵交互 action 使用的动画通道。
 * 它绕开普通动画优先级队列，但受 PlayerFairyInteractionState 约束。
 */
interface PlayerFairyAnimationScheduler {
    fun playPlayerFairyInteractionAnimation(
        ownerInteractionId: String,
        animation: FairyAnimation
    ): Boolean

    fun stopPlayerFairyInteractionAnimation(ownerInteractionId: String)
}
