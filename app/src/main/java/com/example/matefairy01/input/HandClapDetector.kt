package com.example.matefairy01.input

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.tracking.hand.HandJoint
import com.pico.spatial.tracking.hand.HandPose
import com.pico.spatial.tracking.hand.HandTrackingData
import kotlin.math.sqrt

/**
 * 拍手检测器
 * 用于手眼模式下检测用户拍手动作（3次拍手进入录音模式）
 */
class HandClapDetector(
    private val onClapDetected: () -> Unit
) {
    companion object {
        private const val TAG = "HandClapDetector"
        private const val CLAP_DISTANCE_THRESHOLD = 0.15f // 拍手距离阈值（米）
        private const val CLAP_COOLDOWN_MS = 300L // 单次拍手冷却时间（毫秒）
        private const val REQUIRED_CLAP_COUNT = 3 // 需要的拍手次数
        private const val CLAP_SEQUENCE_TIMEOUT_MS = 2000L // 拍手序列超时时间（毫秒）
    }

    private var lastClapTime: Long = 0L
    private var clapCount = 0
    private var sequenceStartTime: Long = 0L
    private var wasHandsClose = false

    private val mainHandler = Handler(Looper.getMainLooper())
    private var resetRunnable: Runnable? = null

    /**
     * 处理手部追踪数据，检测拍手动作
     * 需要在每帧 update 中调用
     */
    fun processHandTrackingData(handTrackingData: HandTrackingData) {
        val leftHand = handTrackingData.left
        val rightHand = handTrackingData.right

        if (leftHand == null || rightHand == null) {
            return
        }

        // 获取双手掌心位置
        val leftPalm = getPalmPosition(leftHand)
        val rightPalm = getPalmPosition(rightHand)

        if (leftPalm == null || rightPalm == null) {
            return
        }

        // 计算双手掌心距离
        val distance = calculateDistance(leftPalm, rightPalm)
        val isHandsClose = distance < CLAP_DISTANCE_THRESHOLD

        // 检测拍手：从分开到靠近
        if (isHandsClose && !wasHandsClose) {
            val currentTime = SystemClock.elapsedRealtime()

            // 检查冷却时间
            if (currentTime - lastClapTime > CLAP_COOLDOWN_MS) {
                detectClap(currentTime)
            }
        }

        wasHandsClose = isHandsClose
    }

    private fun getPalmPosition(handPose: HandPose): Vector3? {
        return try {
            handPose[HandJoint.Index.PALM]?.position
        } catch (e: Exception) {
            null
        }
    }

    private fun calculateDistance(a: Vector3, b: Vector3): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        val dz = a.z - b.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    private fun detectClap(currentTime: Long) {
        lastClapTime = currentTime

        // 如果是序列中的第一次拍手，记录序列开始时间
        if (clapCount == 0) {
            sequenceStartTime = currentTime
            startSequenceTimeout()
        }

        clapCount++
        Log.d(TAG, "Clap detected! Count: $clapCount")

        // 检查是否达到目标次数
        if (clapCount >= REQUIRED_CLAP_COUNT) {
            Log.d(TAG, "Triple clap detected! Triggering voice input...")
            onClapDetected()
            resetClapSequence()
        }
    }

    private fun startSequenceTimeout() {
        resetRunnable?.let { mainHandler.removeCallbacks(it) }
        resetRunnable = Runnable {
            if (clapCount > 0 && clapCount < REQUIRED_CLAP_COUNT) {
                Log.d(TAG, "Clap sequence timed out. Count: $clapCount")
                resetClapSequence()
            }
        }.also {
            mainHandler.postDelayed(it, CLAP_SEQUENCE_TIMEOUT_MS)
        }
    }

    private fun resetClapSequence() {
        clapCount = 0
        sequenceStartTime = 0L
        resetRunnable?.let { mainHandler.removeCallbacks(it) }
        resetRunnable = null
    }

    /**
     * 清理资源
     */
    fun cleanup() {
        resetRunnable?.let { mainHandler.removeCallbacks(it) }
    }
}
