package com.example.matefairy01.input

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import com.pico.spatial.tracking.controller.ControllerActionData
import com.pico.spatial.tracking.controller.ControllerTrackingProvider

/**
 * 输入控制器管理器
 * 负责处理手柄输入检测：
 * 1. 长按右侧 trigger 1秒：开始录音（语音输入），松开后结束
 * 2. 连续点按两下右侧 trigger：弹出文本输入框
 */
class InputControllerManager(
    private val textInputProvider: TextInputProvider,
    private val voiceInputProvider: VoiceInputProvider,
    private val onTextInputResult: (String) -> Unit,
    private val onVoiceInputResult: (String) -> Unit
) {
    companion object {
        private const val LONG_PRESS_THRESHOLD_MS = 1000L // 长按阈值：1秒
        private const val DOUBLE_CLICK_THRESHOLD_MS = 500L // 双击阈值：500毫秒
        private const val TAG = "InputControllerManager"
    }

    private var lastTriggerDownTime: Long = 0L
    private var lastTriggerUpTime: Long = 0L
    private var isTriggerPressed = false
    private var isLongPressTriggered = false
    private var clickCount = 0
    private var lastClickTime: Long = 0L

    private val mainHandler = Handler(Looper.getMainLooper())
    private var longPressRunnable: Runnable? = null
    private var doubleClickRunnable: Runnable? = null

    /**
     * 当前激活的输入提供者
     */
    var activeInputProvider: IUserInputProvider? = null
        private set

    /**
     * 处理控制器动作数据
     * 需要在每帧 update 中调用
     */
    fun processControllerAction(actionData: ControllerActionData) {
        val rightTriggerPressed = actionData.right?.triggerPressed ?: false

        if (rightTriggerPressed && !isTriggerPressed) {
            // Trigger 按下事件
            onTriggerDown()
        } else if (!rightTriggerPressed && isTriggerPressed) {
            // Trigger 释放事件
            onTriggerUp()
        }

        isTriggerPressed = rightTriggerPressed
    }

    private fun onTriggerDown() {
        lastTriggerDownTime = SystemClock.elapsedRealtime()
        isLongPressTriggered = false

        // 启动长按检测
        longPressRunnable = Runnable {
            if (isTriggerPressed) {
                // 触发长按：开始语音输入
                isLongPressTriggered = true
                startVoiceInput()
            }
        }.also {
            mainHandler.postDelayed(it, LONG_PRESS_THRESHOLD_MS)
        }
    }

    private fun onTriggerUp() {
        val pressDuration = SystemClock.elapsedRealtime() - lastTriggerDownTime

        // 取消长按检测
        longPressRunnable?.let { mainHandler.removeCallbacks(it) }
        longPressRunnable = null

        if (isLongPressTriggered) {
            // 长按结束：停止语音输入
            stopVoiceInput()
        } else if (pressDuration < LONG_PRESS_THRESHOLD_MS) {
            // 短按：检测双击
            handleShortClick()
        }
    }

    private fun handleShortClick() {
        val currentTime = SystemClock.elapsedRealtime()

        if (currentTime - lastClickTime <= DOUBLE_CLICK_THRESHOLD_MS) {
            // 检测到双击
            clickCount++
            if (clickCount >= 2) {
                // 触发双击：打开文本输入框
                cancelPendingDoubleClickDetection()
                startTextInput()
                clickCount = 0
            }
        } else {
            // 新的单击序列开始
            clickCount = 1
            lastClickTime = currentTime

            // 启动双击检测超时
            doubleClickRunnable = Runnable {
                // 双击超时，重置计数
                clickCount = 0
            }.also {
                mainHandler.postDelayed(it, DOUBLE_CLICK_THRESHOLD_MS)
            }
        }
    }

    private fun cancelPendingDoubleClickDetection() {
        doubleClickRunnable?.let { mainHandler.removeCallbacks(it) }
        doubleClickRunnable = null
    }

    private fun startTextInput() {
        Log.d(TAG, "Starting text input")
        activeInputProvider = textInputProvider
        
        textInputProvider.startListening { result ->
            Log.d(TAG, "Text input result: $result")
            onTextInputResult(result)
            activeInputProvider = null
        }
    }

    private fun startVoiceInput() {
        Log.d(TAG, "Starting voice input")
        activeInputProvider = voiceInputProvider
        voiceInputProvider.startListening { result ->
            Log.d(TAG, "Voice input result: $result")
            onVoiceInputResult(result)
            activeInputProvider = null
        }
    }

    private fun stopVoiceInput() {
        Log.d(TAG, "Stopping voice input")
        voiceInputProvider.stopListening()
        activeInputProvider = null
    }

    /**
     * 清理资源
     */
    fun cleanup() {
        longPressRunnable?.let { mainHandler.removeCallbacks(it) }
        doubleClickRunnable?.let { mainHandler.removeCallbacks(it) }
        textInputProvider.stopListening()
        voiceInputProvider.stopListening()
        activeInputProvider = null
    }
}
