package com.example.matefairy01.input

/**
 * 用户输入抽象层
 * 支持语音输入和文本输入两种模式，方便后续扩展
 */
interface IUserInputProvider {
    /**
     * 开始监听用户输入
     * @param onResult 输入结果回调
     */
    fun startListening(onResult: (String) -> Unit)

    /**
     * 停止监听用户输入
     */
    fun stopListening()

    /**
     * 当前是否正在监听输入
     */
    fun isListening(): Boolean

    /**
     * 输入模式类型
     */
    val inputMode: InputMode
}

/**
 * 输入模式枚举
 */
enum class InputMode {
    VOICE,  // 语音输入
    TEXT    // 文本输入
}
