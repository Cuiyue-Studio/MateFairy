package com.example.matefairy01.audio

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import java.io.File

/**
 * 独立的音乐模块，负责播放和调度背景音乐(BGM)及相关音频资源。
 * 与 3D 实体解耦，主要用于提供游戏/应用的全局环境音效或背景音乐。
 */
class MusicModule(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var currentBgmIdentifier: String? = null
    private var currentVolume: Float = 1.0f

    companion object {
        private const val TAG = "MusicModule"
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
    }
}
