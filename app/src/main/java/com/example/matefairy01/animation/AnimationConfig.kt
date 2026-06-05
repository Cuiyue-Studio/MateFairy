package com.example.matefairy01.animation

enum class AnimationPriority {
    INSTRUCTION, // 1. 最高优：指令动作 (非任务型/任务型)
    EMOTION,     // 2. 中优先级：情绪反应
    BASE         // 3. 兜底：常驻动画 (闲置/移动)
}

enum class AnimationType(val priority: AnimationPriority) {
    BASE_IDLE(AnimationPriority.BASE),
    BASE_MOVING(AnimationPriority.BASE),
    EMOTION_REACTION(AnimationPriority.EMOTION),
    NON_TASK_ACTION(AnimationPriority.INSTRUCTION)
}

/**
 * 动画配置映射
 *
 * 根据模型文件 (pico_robot_animated.glb) 中的轨道索引映射：
 * 0: 01_idle
 * 1: 02_jump
 * 2: 03_look_around
 * 3: 04_walk_forward
 * 4: 05_wave
 * 5: 06_mad_action
 * 6: Action (无用轨道，忽略)
 * 7: Action.001 (无用轨道，忽略)
 */
enum class FairyAnimation(
    val trackIndex: Int, 
    val animName: String, 
    val type: AnimationType, 
    val durationMs: Long,
    val intent: String? = null // 用于自动注入到 LLM Prompt 中的意图名称
) {
    // --- 情绪动画 (EMOTION) ---
    MAD_ACTION(5, "Mad Action", AnimationType.EMOTION_REACTION, 3000L, "angry"),
    HAPPY_ACTION(6, "Happy Action", AnimationType.EMOTION_REACTION, 3000L, "happy"),

    // --- 指令动画 (ACTION) ---
    // 目前指令动画暂未就绪，留空。后期直接在这里增加即可。
    DANCE_ACTION(7, "Dance Action", AnimationType.NON_TASK_ACTION, 4000L, "dance"),

    // --- 常驻动画 (BASE) ---
    // 这里全是原有的老动画，均作为兜底的基础表现
    STANDBY_MODE(0, "Standby", AnimationType.BASE_IDLE, 3000L),
    SPIN_LEAP(1, "Spin Leap", AnimationType.BASE_MOVING, 2000L),
    CURIOUS_LOOK(2, "Curious Look", AnimationType.BASE_IDLE, 2500L),
    TURBO_DASH(3, "Turbo Dash", AnimationType.BASE_MOVING, 1500L),
    HELLO_WAVE(4, "Hello Wave", AnimationType.BASE_IDLE, 2000L)
}

object AnimationConfig {
    val idleAnimations = FairyAnimation.values().filter { it.type == AnimationType.BASE_IDLE }
    val movingAnimations = FairyAnimation.values().filter { it.type == AnimationType.BASE_MOVING }

    // 提供给 LLM 的动态支持列表
    val supportedEmotions: List<String> = (listOf("neutral", "sad", "surprised", "thinking") + 
        FairyAnimation.values().filter { it.type == AnimationType.EMOTION_REACTION }.mapNotNull { it.intent }).distinct()
    
    val supportedActions: List<String> = (listOf("none", "fetch_ball", "play-football") +
        FairyAnimation.values().filter { it.type == AnimationType.NON_TASK_ACTION }.mapNotNull { it.intent }).distinct()

    fun getAnimationByEmotion(emotion: String): FairyAnimation? {
        return FairyAnimation.values().find { it.type == AnimationType.EMOTION_REACTION && it.intent == emotion }
    }

    fun getAnimationByAction(action: String): FairyAnimation? {
        return FairyAnimation.values().find { it.type == AnimationType.NON_TASK_ACTION && it.intent == action }
    }
}
