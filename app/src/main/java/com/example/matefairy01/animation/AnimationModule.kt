package com.example.matefairy01.animation

import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.resource.AnimationResource

/**
 * 独立的动画管理模块，提供对外调度接口，与AI逻辑解耦
 */
class AnimationModule : AnimationController {
    private var skinnedMeshEntity: Entity? = null
    private var animationResources: Array<AnimationResource>? = null
    
    var currentPlayingAnim: FairyAnimation? = null
        private set

    // 频控相关
    private var lastIdleAnimPlayTime: Long = 0
    // 静止动画的冷却时间（毫秒），避免频繁执行显得多动症
    private val IDLE_ANIM_COOLDOWN_MS = 6000L 

    override fun initialize(robotEntity: Entity) {
        val skinnedMeshEntities = robotEntity.findSkinnedMeshEntity().toList()
        if (skinnedMeshEntities.isNotEmpty()) {
            this.skinnedMeshEntity = skinnedMeshEntities.first()
            this.animationResources = this.skinnedMeshEntity?.getAnimationResources()
        }
    }

    /**
     * 对外提供的通用播放接口，AI模块也可调用
     */
    override fun playAnimation(animation: FairyAnimation) {
        val resources = animationResources ?: return
        val meshEntity = skinnedMeshEntity ?: return
        
        if (animation.trackIndex in resources.indices) {
            meshEntity.playAnimation(resources[animation.trackIndex])
            currentPlayingAnim = animation
            
            if (animation.type == AnimationType.IDLE) {
                lastIdleAnimPlayTime = System.currentTimeMillis()
            }
        }
    }

    fun stopAllAnimations() {
        skinnedMeshEntity?.stopAllAnimations()
        currentPlayingAnim = null
    }

    override fun cleanup() {
        stopAllAnimations()
        animationResources?.forEach { it.close() }
        animationResources = null
        skinnedMeshEntity = null
    }

    /**
     * 判断是否可以播放静止时动画（频控）
     */
    fun canPlayIdleAnimation(): Boolean {
        return System.currentTimeMillis() - lastIdleAnimPlayTime > IDLE_ANIM_COOLDOWN_MS
    }

    /**
     * 随机播放一个静止时动画
     * @return 播放的动画，如果没有播放则返回 null
     */
    override fun playRandomIdleAnimation(): FairyAnimation? {
        if (!canPlayIdleAnimation()) return null
        
        // 可配置的概率，这里假设冷却满足就播放，或者也可以加个随机数
        // val rand = kotlin.random.Random.nextFloat()
        // if (rand > 0.6f) return null // 60% 概率播放
        
        val anim = AnimationConfig.idleAnimations.random()
        playAnimation(anim)
        return anim
    }

    /**
     * 随机播放一个运动时动画（极速冲刺等可以在每次随机运动时播放）
     */
    override fun playRandomMovingAnimation(): FairyAnimation? {
        val anim = AnimationConfig.movingAnimations.random()
        playAnimation(anim)
        return anim
    }
}
