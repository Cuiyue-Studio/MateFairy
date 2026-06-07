package com.example.matefairy01.audio

import android.util.Log
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.ObjectAudioComponent
import com.pico.spatial.core.ecs.audio.AudioPlayerController
import com.pico.spatial.core.ecs.audio.Directivity
import com.pico.spatial.core.ecs.audio.DistanceAttenuationMode
import com.pico.spatial.core.ecs.resource.AudioResource
import com.pico.spatial.core.ecs.LoadType
import kotlinx.coroutines.*
import kotlin.random.Random

/**
 * 解耦合的精灵语音模块 (3D空间音频版)
 * 负责管理精灵的两种语音播放：
 * 1. 动作语音（优先级高，立即播放并打断其他语音）
 * 2. 常驻语音（在指定的时间区间内随机播放，带频控逻辑）
 */
class FairyAudioModule(private val fairyEntity: Entity) {
    private var actionPlayer: AudioPlayerController? = null
    private var residentPlayer: AudioPlayerController? = null
    
    private var residentJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    private var residentAudioList: List<String> = emptyList()
    private var minIntervalMs: Long = 10000L
    private var maxIntervalMs: Long = 30000L
    private var isResidentLoopRunning = false
    
    // 音频资源缓存，避免重复加载
    private val audioResources = mutableMapOf<String, AudioResource>()
    
    companion object {
        private const val TAG = "FairyAudioModule"
    }
    
    init {
        // 确保实体上挂载了 3D 空间音效组件，以使音频能随实体位置发生空间衰减和方向变化
        if (!fairyEntity.components.has(ObjectAudioComponent::class.java)) {
            val objectAudioComponent = ObjectAudioComponent(
                volume = 1.0f,
                directivity = Directivity(pattern = 0.235f, sharpness = 0.675f),
                distanceAttenuationMode = DistanceAttenuationMode.INVERSE_SQUARED,
                reverbVolume = 0.5f
            )
            fairyEntity.components.set(objectAudioComponent)
        }
    }
    
    private fun getAudioResource(fileName: String): AudioResource {
        return audioResources.getOrPut(fileName) {
            AudioResource.load(fileName, fileName, LoadType.FROM_ASSETS)
        }
    }
    
    /**
     * 播放动作触发的语音（立即播放，高优先级）
     * 会打断当前正在播放的动作语音和常驻语音
     * @param fileName assets 目录下的音频文件名
     * @param volume 音量 (0.0 - 1.0)
     */
    fun playActionAudio(fileName: String, volume: Float = 1.0f) {
        Log.d(TAG, "Playing action audio: $fileName")
        stopActionAudio()
        
        // 动作语音优先级高，打断当前的常驻语音播放
        stopResidentAudioPlayback()
        
        try {
            val resource = getAudioResource(fileName)
            actionPlayer = fairyEntity.playAudio(resource)?.apply {
                setVolume(volume)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play action audio: $fileName", e)
        }
    }
    
    /**
     * 停止当前的动作语音
     */
    fun stopActionAudio() {
        actionPlayer?.let {
            if (it.isPlaying()) {
                it.stop()
            }
            it.close()
        }
        actionPlayer = null
    }

    /**
     * 设置常驻语音资源列表及频控参数
     * @param audioList assets 目录下的常驻语音文件列表
     * @param minInterval 最小触发间隔（毫秒）
     * @param maxInterval 最大触发间隔（毫秒）
     */
    fun setResidentConfig(audioList: List<String>, minInterval: Long = 10000L, maxInterval: Long = 30000L) {
        this.residentAudioList = audioList
        this.minIntervalMs = minInterval
        this.maxIntervalMs = maxInterval
    }
    
    /**
     * 开启常驻语音的随机播放循环
     */
    fun startResidentAudioLoop() {
        if (isResidentLoopRunning) return
        isResidentLoopRunning = true
        
        residentJob = scope.launch {
            while (isActive && isResidentLoopRunning) {
                // 计算本次随机等待时间
                val waitTime = if (maxIntervalMs > minIntervalMs) {
                    Random.nextLong(minIntervalMs, maxIntervalMs)
                } else {
                    minIntervalMs
                }
                
                delay(waitTime)
                
                // 如果正在播放动作语音，或者列表为空，则跳过本次播放，进入下一次等待
                if (actionPlayer?.isPlaying() == true || residentAudioList.isEmpty()) {
                    continue
                }
                
                // 随机挑选一段常驻语音
                val randomAudio = residentAudioList.random()
                playResidentAudioSuspend(randomAudio)
            }
        }
    }
    
    /**
     * 挂起函数：播放常驻语音并等待播放完成
     */
    private suspend fun playResidentAudioSuspend(fileName: String, volume: Float = 1.0f) {
        Log.d(TAG, "Playing resident audio: $fileName")
        stopResidentAudioPlayback()
        
        var player: AudioPlayerController? = null
        try {
            val resource = getAudioResource(fileName)
            player = fairyEntity.playAudio(resource)
            if (player == null) return
            
            player.setVolume(volume)
            residentPlayer = player
            
            // 轮询检查是否播放完毕，100ms 检查一次
            while (kotlin.coroutines.coroutineContext.isActive && residentPlayer == player && player.isPlaying()) {
                delay(100)
            }
        } catch (e: CancellationException) {
            // 协程被取消（如调用了 stopResidentAudioLoop）
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play resident audio: $fileName", e)
        } finally {
            // 如果退出时当前的 player 仍然是正在播放的常驻语音，则停止并清理
            if (residentPlayer == player) {
                stopResidentAudioPlayback()
            }
        }
    }

    /**
     * 停止正在播放的常驻语音实体，但不关闭循环调度
     */
    private fun stopResidentAudioPlayback() {
        residentPlayer?.let {
            if (it.isPlaying()) {
                it.stop()
            }
            it.close()
        }
        residentPlayer = null
    }

    /**
     * 停止常驻语音循环调度及当前播放的常驻语音
     */
    fun stopResidentAudioLoop() {
        isResidentLoopRunning = false
        residentJob?.cancel()
        residentJob = null
        stopResidentAudioPlayback()
    }
    
    /**
     * 销毁模块，释放所有资源和协程
     */
    fun destroy() {
        stopActionAudio()
        stopResidentAudioLoop()
        scope.cancel()
        
        // 释放所有缓存的音频资源
        audioResources.values.forEach { it.close() }
        audioResources.clear()
    }
}
