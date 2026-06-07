package com.example.matefairy01.animation

import android.util.Log
import com.example.matefairy01.interaction.InteractionActionListener
import com.example.matefairy01.interaction.InteractionActionRuntimeDependencies
import com.example.matefairy01.interaction.InteractionActionSource
import com.example.matefairy01.interaction.InteractionActionStatus
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.resource.AnimationResource
import kotlinx.coroutines.*

/**
 * 统一动画调度中心 (Master Animation Scheduler)
 * 具有严格优先级和生命周期的状态机：
 * 1. 指令层 (Instruction): 最高优，独占控制权。
 * 2. 情绪反应层 (Emotion Reaction): 中优，单次触发，播完降级。
 * 3. 常驻基础层 (Base Idle/Moving): 最低优，永远兜底。
 */
class AnimationModule : AnimationController, InteractionActionListener {
    companion object {
        private const val TAG = "AnimationModule"
    }

    private var skinnedMeshEntity: Entity? = null
    private var animationResources: Array<AnimationResource>? = null
    
    var currentPlayingAnim: FairyAnimation? = null
        private set

    private val moduleScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var activeJob: Job? = null
    private var queuedInstruction: FairyAnimation? = null

    // 频控相关
    private var lastIdleAnimPlayTime: Long = 0
    // 静止动画的冷却时间（毫秒），避免频繁执行显得多动症
    private val IDLE_ANIM_COOLDOWN_MS = 6000L 

    override fun initialize(robotEntity: Entity) {
        val skinnedMeshEntities = robotEntity.findSkinnedMeshEntity().toList()
        if (skinnedMeshEntities.isNotEmpty()) {
            this.skinnedMeshEntity = skinnedMeshEntities.first()
            this.animationResources = this.skinnedMeshEntity?.getAnimationResources()
            Log.d(TAG, "Initialized animation resources: count=${animationResources?.size ?: 0}")
        } else {
            Log.w(TAG, "No skinned mesh entity found; animations will not play")
        }
    }

    /**
     * 对外提供的通用播放接口，内置优先级与序列队列处理
     */
    override fun playAnimation(animation: FairyAnimation) {
        if (InteractionActionRuntimeDependencies.lockState.isLocked) {
            Log.d(TAG, "Ignore ${animation.animName}: interaction action lock is active")
            return
        }

        val resources = animationResources
        if (resources == null) {
            Log.w(TAG, "Ignore ${animation.animName}: animation resources not initialized")
            return
        }
        if (animation.trackIndex !in resources.indices) {
            Log.w(
                TAG,
                "Ignore ${animation.animName}: trackIndex=${animation.trackIndex}, resources=${resources.size}"
            )
            return
        }

        val incomingPriority = animation.type.priority
        val currentPriority = currentPlayingAnim?.type?.priority ?: AnimationPriority.BASE

        when (incomingPriority) {
            AnimationPriority.INSTRUCTION -> {
                if (currentPriority == AnimationPriority.EMOTION) {
                    // 如果正在播放情绪，将指令加入队列，串行执行
                    queuedInstruction = animation
                    return
                }
                // 否则直接打断播放
                executePlay(animation)
            }
            AnimationPriority.EMOTION -> {
                if (currentPriority == AnimationPriority.INSTRUCTION) {
                    // 正在执行高优指令，忽略情绪
                    return
                }
                // 打断兜底动画，播放情绪
                executePlay(animation)
            }
            AnimationPriority.BASE -> {
                if (currentPriority == AnimationPriority.INSTRUCTION || currentPriority == AnimationPriority.EMOTION) {
                    // 忽略基础动画请求，维持高优状态
                    return
                }
                executePlay(animation)
            }
        }
    }

    private fun executePlay(animation: FairyAnimation) {
        val resources = animationResources ?: return
        val meshEntity = skinnedMeshEntity ?: return

        activeJob?.cancel() // 取消之前的计时器

        Log.d(TAG, "Play ${animation.animName}, type=${animation.type}, trackIndex=${animation.trackIndex}")
        meshEntity.playAnimation(resources[animation.trackIndex])
        currentPlayingAnim = animation
        
        if (animation.type == AnimationType.BASE_IDLE) {
            lastIdleAnimPlayTime = System.currentTimeMillis()
        }

        // 状态维持与自动降级：情绪或指令，开启协程等待播完
        if (animation.type.priority == AnimationPriority.EMOTION || animation.type.priority == AnimationPriority.INSTRUCTION) {
            activeJob = moduleScope.launch {
                delay(animation.durationMs)
                onAnimationComplete(animation)
            }
        }
    }

    private fun onAnimationComplete(animation: FairyAnimation) {
        if (currentPlayingAnim != animation) return // 已经被打断

        if (animation.type.priority == AnimationPriority.EMOTION) {
            // 情绪播完了，看有没有排队的指令
            val queued = queuedInstruction
            if (queued != null) {
                queuedInstruction = null
                executePlay(queued)
                return
            }
        }
        
        // 自动降级到 BASE (清除状态，等待 BehaviorSystem 再次请求 BASE)
        currentPlayingAnim = null
    }

    fun stopAllAnimations() {
        skinnedMeshEntity?.stopAllAnimations()
        currentPlayingAnim = null
        activeJob?.cancel()
        queuedInstruction = null
    }

    override fun cleanup() {
        InteractionActionRuntimeDependencies.lockState.removeListener(this)
        stopAllAnimations()
        moduleScope.cancel()
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
     */
    override fun playRandomIdleAnimation(): FairyAnimation? {
        if (!canPlayIdleAnimation()) return null
        
        val anim = AnimationConfig.idleAnimations.randomOrNull() ?: return null
        playAnimation(anim)
        return anim
    }

    /**
     * 随机播放一个运动时动画
     */
    override fun playRandomMovingAnimation(): FairyAnimation? {
        val anim = AnimationConfig.movingAnimations.randomOrNull() ?: return null
        playAnimation(anim)
        return anim
    }

    override fun onActionStarted(
        actionId: String,
        controllerId: String,
        source: InteractionActionSource
    ) {
        stopAllAnimations()
    }

    override fun onActionFinished(
        actionId: String,
        controllerId: String,
        source: InteractionActionSource,
        status: InteractionActionStatus
    ) {
        Log.d(TAG, "Interaction action finished: action=$actionId, status=$status")
    }
}
