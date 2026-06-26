package com.example.matefairy01.audio

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.LoadType
import com.pico.spatial.core.ecs.ObjectAudioComponent
import com.pico.spatial.core.ecs.audio.AudioPlayerController
import com.pico.spatial.core.ecs.audio.Directivity
import com.pico.spatial.core.ecs.audio.DistanceAttenuationMode
import com.pico.spatial.core.ecs.resource.AudioResource
import kotlin.random.Random

/**
 * 独立的音乐模块，负责播放和调度背景音乐(BGM)及相关音频资源。
 * 与 3D 实体解耦，主要用于提供游戏/应用的全局环境音效或背景音乐。
 */
class MusicModule(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var spatialMusicPlayer: AudioPlayerController? = null
    private var spatialSfxPlayer: AudioPlayerController? = null
    private var currentBgmIdentifier: String? = null
    private var currentVolume: Float = 1.0f
    private val audioResources = mutableMapOf<String, AudioResource>()
    private var spatialMusicPlaylist: List<String> = emptyList()
    private var rubberDuckSfxList: List<String> = emptyList()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var spatialMusicTrackIndex: Int = -1
    private var spatialMusicScheduleToken: Int = 0
    private var spatialMusicEntity: Entity? = null
    private var spatialMusicVolume: Float = 1.0f

    companion object {
        private const val TAG = "MusicModule"
        private const val AUTO_ADVANCE_GRACE_MS = 250L
        private const val AUTO_ADVANCE_RETRY_MS = 500L
        private const val DEFAULT_SPATIAL_MUSIC_VOLUME = 0.18f
        private const val OBJECT_AUDIO_SOURCE_VOLUME = 0.85f
        private const val RUBBER_DUCK_SFX_DURATION_MS = 1500L
    }

    fun setSpatialMusicPlaylist(fileNames: List<String>) {
        val filtered = fileNames.filter { it.isNotBlank() }
        if (spatialMusicPlaylist != filtered) {
            spatialMusicTrackIndex = -1
        }
        spatialMusicPlaylist = filtered
    }

    fun setRubberDuckSfxList(fileNames: List<String>) {
        rubberDuckSfxList = fileNames.filter { it.isNotBlank() }
    }

    fun playRandomSpatialMusicAt(entity: Entity, volume: Float = DEFAULT_SPATIAL_MUSIC_VOLUME): String? {
        return playNextSpatialMusicAt(entity, volume)
    }

    fun playNextSpatialMusicAt(entity: Entity, volume: Float = DEFAULT_SPATIAL_MUSIC_VOLUME): String? {
        if (spatialMusicPlaylist.isEmpty()) {
            Log.w(TAG, "Spatial music playlist is empty; skip boombox playback")
            return null
        }
        val nextIndex = (spatialMusicTrackIndex + 1).floorMod(spatialMusicPlaylist.size)
        val fileName = spatialMusicPlaylist[nextIndex]
        val didStart = playSpatialMusicAt(entity, fileName, volume)
        return if (didStart) {
            spatialMusicTrackIndex = nextIndex
            spatialMusicEntity = entity
            spatialMusicVolume = volume.coerceIn(0f, 1f)
            scheduleNextSpatialTrack(entity, fileName)
            fileName
        } else {
            null
        }
    }

    fun stopSpatialMusic() {
        spatialMusicScheduleToken++
        spatialMusicEntity = null
        spatialMusicPlayer?.let {
            if (it.isPlaying()) it.stop()
            it.close()
        }
        spatialMusicPlayer = null
        stopBgm()
    }

    fun isSpatialMusicPlaying(): Boolean {
        return spatialMusicPlayer?.isPlaying() == true
    }

    fun playRandomRubberDuckSfxAt(entity: Entity, volume: Float = 1.0f): String? {
        if (rubberDuckSfxList.isEmpty()) {
            Log.w(TAG, "Rubber duck SFX list is empty; skip squeak playback")
            return null
        }
        val fileName = rubberDuckSfxList.random(Random.Default)
        val player = playSpatialSfxAt(entity, fileName, volume)
        player?.let { p ->
            mainHandler.postDelayed(
                {
                    if (p.isPlaying()) {
                        p.stop()
                    }
                },
                RUBBER_DUCK_SFX_DURATION_MS
            )
        }
        return fileName
    }

    private fun playSpatialMusicAt(entity: Entity, fileName: String, volume: Float): Boolean {
        releaseSpatialMusicPlayer()
        return runCatching {
            ensureObjectAudio(entity)
            val resource = getSpatialAudioResource(fileName)
            spatialMusicPlayer = entity.prepareAudio(resource)?.apply {
                setVolume(volume.coerceIn(0f, 1f))
                setLoop(false)
                play()
            }
            Log.d(TAG, "Started spatial music at entity: $fileName")
            spatialMusicPlayer != null
        }.onFailure {
            Log.e(TAG, "Failed to play spatial music: $fileName", it)
        }.getOrDefault(false)
    }

    private fun playSpatialSfxAt(entity: Entity, fileName: String, volume: Float): AudioPlayerController? {
        spatialSfxPlayer?.let {
            if (it.isPlaying()) it.stop()
            it.close()
        }
        spatialSfxPlayer = null
        runCatching {
            ensureObjectAudio(entity)
            val resource = getSpatialAudioResource(fileName)
            spatialSfxPlayer = entity.playAudio(resource)?.apply {
                setVolume(volume.coerceIn(0f, 1f))
            }
            Log.d(TAG, "Started spatial SFX at entity: $fileName")
        }.onFailure {
            Log.e(TAG, "Failed to play spatial SFX: $fileName", it)
        }
        return spatialSfxPlayer
    }

    private fun ensureObjectAudio(entity: Entity) {
        entity.components.set(
            ObjectAudioComponent(
                volume = OBJECT_AUDIO_SOURCE_VOLUME,
                directivity = Directivity(pattern = 0f, sharpness = 0f),
                distanceAttenuationMode = DistanceAttenuationMode.INVERSE_SQUARED,
                reverbVolume = 0f
            )
        )
    }

    private fun getSpatialAudioResource(fileName: String): AudioResource {
        return audioResources.getOrPut(fileName) {
            AudioResource.load(fileName, "asset://$fileName", LoadType.FROM_ASSETS)
        }
    }

    private fun scheduleNextSpatialTrack(entity: Entity, fileName: String) {
        val durationMs = getAssetAudioDurationMs(fileName)
        if (durationMs <= 0L) {
            Log.w(TAG, "Cannot determine duration for $fileName; spatial music will not auto-advance")
            return
        }
        val token = ++spatialMusicScheduleToken
        mainHandler.postDelayed(
            {
                if (token != spatialMusicScheduleToken || spatialMusicEntity != entity) {
                    return@postDelayed
                }
                if (spatialMusicPlayer?.isPlaying() == true) {
                    retryAutoAdvance(token, entity)
                } else {
                    playNextSpatialMusicAt(entity, spatialMusicVolume)
                }
            },
            durationMs + AUTO_ADVANCE_GRACE_MS
        )
    }

    private fun retryAutoAdvance(token: Int, entity: Entity) {
        mainHandler.postDelayed(
            {
                if (token == spatialMusicScheduleToken && spatialMusicEntity == entity) {
                    playNextSpatialMusicAt(entity, spatialMusicVolume)
                }
            },
            AUTO_ADVANCE_RETRY_MS
        )
    }

    private fun getAssetAudioDurationMs(fileName: String): Long {
        return runCatching {
            context.assets.openFd(fileName).use { afd ->
                MediaMetadataRetriever().use { retriever ->
                    retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                        ?.toLongOrNull()
                        ?: 0L
                }
            }
        }.onFailure {
            Log.w(TAG, "Failed to read audio duration for $fileName", it)
        }.getOrDefault(0L)
    }

    private fun releaseSpatialMusicPlayer() {
        spatialMusicPlayer?.let {
            if (it.isPlaying()) it.stop()
            it.close()
        }
        spatialMusicPlayer = null
    }

    private fun Int.floorMod(divisor: Int): Int {
        return ((this % divisor) + divisor) % divisor
    }

    /**
     * 播放 assets 目录下的音乐文件
     * @param fileName assets 目录下的文件名（如 "bgm.mp3"）
     * @param loop 是否循环播放
     * @param volume 音量 (0.0 - 1.0)
     */
    fun playBgmFromAsset(fileName: String, loop: Boolean = true, volume: Float = 1.0f) {
        if (currentBgmIdentifier == fileName && mediaPlayer?.isPlaying == true) {
            Log.d(TAG, "Already playing $fileName")
            return
        }

        try {
            stopBgm()
            
            val afd = context.assets.openFd(fileName)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
                isLooping = loop
                currentVolume = volume
                setVolume(volume, volume)
                prepare()
                start()
            }
            currentBgmIdentifier = fileName
            Log.d(TAG, "Started playing BGM from asset: $fileName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play BGM from asset: $fileName", e)
        }
    }
    
    /**
     * 播放本地或网络 URI 的音乐文件
     * @param uri 音乐文件的 URI
     * @param loop 是否循环播放
     * @param volume 音量 (0.0 - 1.0)
     */
    fun playBgmFromUri(uri: String, loop: Boolean = true, volume: Float = 1.0f) {
        if (currentBgmIdentifier == uri && mediaPlayer?.isPlaying == true) {
            Log.d(TAG, "Already playing $uri")
            return
        }
        
        try {
            stopBgm()
            
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, Uri.parse(uri))
                isLooping = loop
                currentVolume = volume
                setVolume(volume, volume)
                prepare()
                start()
            }
            currentBgmIdentifier = uri
            Log.d(TAG, "Started playing BGM from URI: $uri")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play BGM from URI: $uri", e)
        }
    }

    /**
     * 暂停当前播放的 BGM
     */
    fun pauseBgm() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                Log.d(TAG, "Paused BGM")
            }
        }
    }

    /**
     * 恢复播放暂停的 BGM
     */
    fun resumeBgm() {
        mediaPlayer?.let {
            if (!it.isPlaying) {
                it.start()
                Log.d(TAG, "Resumed BGM")
            }
        }
    }

    /**
     * 停止播放并释放资源
     */
    fun stopBgm() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.stop()
            }
            it.release()
        }
        mediaPlayer = null
        currentBgmIdentifier = null
        Log.d(TAG, "Stopped and released BGM")
    }
    
    /**
     * 设置当前播放音量
     * @param volume 音量大小 (0.0 - 1.0)
     */
    fun setVolume(volume: Float) {
        val safeVolume = volume.coerceIn(0.0f, 1.0f)
        currentVolume = safeVolume
        mediaPlayer?.setVolume(safeVolume, safeVolume)
    }

    /**
     * 是否正在播放
     */
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }

    /**
     * 销毁模块时调用，清理所有资源
     */
    fun destroy() {
        stopBgm()
        stopSpatialMusic()
        spatialSfxPlayer?.let {
            if (it.isPlaying()) it.stop()
            it.close()
        }
        spatialSfxPlayer = null
        audioResources.values.forEach { it.close() }
        audioResources.clear()
    }
}
