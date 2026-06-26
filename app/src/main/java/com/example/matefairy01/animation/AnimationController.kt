package com.example.matefairy01.animation

import com.pico.spatial.core.ecs.Entity

enum class AnimationFacingPolicy {
    KEEP_BEHAVIOR,
    FACE_PLAYER,
    KEEP_ACTION_TARGET
}

/**
 * 为行为系统和场景层提供稳定的动画控制接口。
 */
interface AnimationController {
    val currentFacingPolicy: AnimationFacingPolicy

    fun initialize(robotEntity: Entity)

    fun playAnimation(animation: FairyAnimation)

    fun playRandomIdleAnimation(): FairyAnimation?

    fun playRandomMovingAnimation(): FairyAnimation?

    fun cleanup()
}

/**
 * 只给 interaction action 内部使用的动画调度接口。
 *
 * 普通对话/随机动画仍走 [AnimationController.playAnimation]，会被 action lock 屏蔽；
 * action 内部动画通过该接口显式绑定 ownerActionId，避免外部动画插入 action 生命周期。
 */
interface ActionAnimationScheduler {
    fun playActionAnimation(
        ownerActionId: String,
        animation: FairyAnimation,
        options: ActionAnimationPlayOptions = ActionAnimationPlayOptions()
    ): Boolean

    fun isActionAnimationPlaying(ownerActionId: String, animation: FairyAnimation): Boolean

    fun startLoopingActionAnimation(ownerActionId: String, animation: FairyAnimation): Boolean

    fun stopLoopingActionAnimation(ownerActionId: String)

    fun subscribeAnimationLifecycle(listener: AnimationLifecycleListener): AnimationLifecycleSubscription
}

data class ActionAnimationPlayOptions(
    val maxDurationSeconds: Float? = null,
    val publishCompletionEvent: Boolean = true
)

data class AnimationLifecycleEvent(
    val ownerActionId: String?,
    val animation: FairyAnimation,
    val state: AnimationLifecycleState,
    val reason: AnimationEndReason? = null
)

enum class AnimationLifecycleState {
    STARTED,
    COMPLETED,
    CANCELLED,
    FAILED
}

enum class AnimationEndReason {
    NATURAL_END,
    CLIPPED,
    INTERRUPTED,
    RESOURCE_MISSING
}

fun interface AnimationLifecycleListener {
    fun onAnimationLifecycleEvent(event: AnimationLifecycleEvent)
}

fun interface AnimationLifecycleSubscription {
    fun cancel()
}
