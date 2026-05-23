package com.example.matefairy01.input

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 文本输入提供者
 * 用于在 Emulator 上调试或用户偏好文本输入时提供文本输入功能
 */
class TextInputProvider : IUserInputProvider {

    private var isActive = false
    private var resultCallback: ((String) -> Unit)? = null

    /**
     * 每次呼起面板时的自增 ID，用于触发刷新位置
     */
    var sessionTag by mutableStateOf(0)
        private set

    /**
     * 当前输入的文本状态
     */
    var currentText by mutableStateOf("")
        private set

    /**
     * 是否显示输入框
     */
    var showInputDialog by mutableStateOf(false)
        private set

    /**
     * 用户的最后活跃时间（用于超时关闭）
     */
    var lastActiveTime by mutableStateOf(0L)
        private set

    override val inputMode: InputMode = InputMode.TEXT

    override fun startListening(onResult: (String) -> Unit) {
        isActive = true
        resultCallback = onResult
        currentText = ""
        sessionTag++
        showInputDialog = true
        refreshActiveTime()
    }

    override fun stopListening() {
        isActive = false
        showInputDialog = false
    }

    override fun isListening(): Boolean = isActive

    /**
     * 更新当前输入文本
     */
    fun updateText(text: String) {
        currentText = text
        refreshActiveTime()
    }

    /**
     * 提交当前输入的文本
     */
    fun submitText() {
        if (currentText.isNotBlank()) {
            resultCallback?.invoke(currentText.trim())
        }
        stopListening()
    }

    /**
     * 取消输入
     */
    fun cancelInput() {
        stopListening()
    }

    /**
     * 刷新用户的活跃时间
     */
    fun refreshActiveTime() {
        lastActiveTime = System.currentTimeMillis()
    }
}
