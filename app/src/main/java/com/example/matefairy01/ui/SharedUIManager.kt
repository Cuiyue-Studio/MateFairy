package com.example.matefairy01.ui

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.matefairy01.input.TextInputProvider
import com.example.matefairy01.input.VoiceInputProvider

/**
 * 全局解耦的 UI 状态管理器。
 * 用于跨 Stage 和 WindowContainer 共享用户级 UI 的状态。
 */
object SharedUIManager {
    lateinit var textInputProvider: TextInputProvider
    lateinit var voiceInputProvider: VoiceInputProvider

    // 控制用户级 UI 窗口的显示状态
    var isUserUIWindowOpen by mutableStateOf(false)

    fun initialize(context: Context) {
        textInputProvider = TextInputProvider()
        voiceInputProvider = VoiceInputProvider(context)
    }
}
