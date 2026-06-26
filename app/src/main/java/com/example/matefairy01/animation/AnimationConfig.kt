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
 * 根据模型文件 (pico_robot_animated_new.glb) 中的有效轨道索引映射：
 * 0: 01_idle
 * 10: 02_jump
 * 14: 03_look_around
 * 18: 04_walk_forward
 * 22: 05_wave
 * 26: 06_mad_action
 * 30: 07_happy_action
 * 34: 08_dance_action
 * 38: 09_sad_action
 * 40: 10_pick_action
 * 42: 11_disco_dancing_action
 * 44: 12_kick_action
 */
enum class FairyAnimation(
    val trackIndex: Int, 
    val animName: String, 
    val type: AnimationType, 
    val durationMs: Long,
    val intent: String? = null // 用于自动注入到 LLM Prompt 中的意图名称
) {
    // --- 情绪动画 (EMOTION) ---
    MAD_ACTION(26, "Mad Action", AnimationType.EMOTION_REACTION, 3000L, "angry"),
    HAPPY_ACTION(30, "Happy Action", AnimationType.EMOTION_REACTION, 3000L, "happy"),
    SAD_ACTION(38, "Sad Action", AnimationType.EMOTION_REACTION, 3000L, "sad"),

    // --- 指令动画 (ACTION) ---
    DANCE_ACTION(34, "Dance Action", AnimationType.NON_TASK_ACTION, 4000L, "dance"),
    PICK_ACTION(40, "Pick Action", AnimationType.NON_TASK_ACTION, 2000L),
    DISCO_DANCING_ACTION(42, "Disco Dancing Action", AnimationType.NON_TASK_ACTION, 4000L, "disco"),
    KICK_ACTION(44, "Kick Action", AnimationType.NON_TASK_ACTION, 2000L),

    // --- 常驻动画 (BASE) ---
    // 这里全是原有的老动画，均作为兜底的基础表现
    STANDBY_MODE(0, "Standby", AnimationType.BASE_IDLE, 3000L),
    SPIN_LEAP(10, "Spin Leap", AnimationType.BASE_MOVING, 2000L),
    CURIOUS_LOOK(14, "Curious Look", AnimationType.BASE_IDLE, 2500L),
    TURBO_DASH(18, "Turbo Dash", AnimationType.BASE_MOVING, 1500L),
    HELLO_WAVE(22, "Hello Wave", AnimationType.BASE_IDLE, 2000L)
}

object AnimationConfig {
    const val fairyModelAssetUri = "asset://pico_robot_animated_new.glb"

    val idleAnimations = FairyAnimation.values().filter { it.type == AnimationType.BASE_IDLE }
    val movingAnimations = FairyAnimation.values().filter { it.type == AnimationType.BASE_MOVING }

    // 提供给 LLM 的动态支持列表
    val supportedEmotions: List<String> = (listOf("neutral", "sad", "surprised", "thinking") + 
        FairyAnimation.values().filter { it.type == AnimationType.EMOTION_REACTION }.mapNotNull { it.intent }).distinct()
    
    val supportedActions: List<String> = (listOf(
        "none",
        "fetch_ball",
        "play-football",
        "start-boombox",
        "stop-boombox",
        // Dialogue-triggered squeeze-rubber-duck is enabled; random and direct gesture entry
        // points remain disabled elsewhere for controlled testing.
        "squeeze-rubber-duck",
        "put-down-rubber-duck",
        "stay-on-chair",
        "leave-chair"
    ) +
        FairyAnimation.values().filter { it.type == AnimationType.NON_TASK_ACTION }.mapNotNull { it.intent }).distinct()

    fun getAnimationByEmotion(emotion: String): FairyAnimation? {
        return FairyAnimation.values().find { it.type == AnimationType.EMOTION_REACTION && it.intent == emotion }
    }

    fun getAnimationByAction(action: String): FairyAnimation? {
        return FairyAnimation.values().find { it.type == AnimationType.NON_TASK_ACTION && it.intent == action }
    }

    val playFootballKickAnimation = FairyAnimation.KICK_ACTION
}
