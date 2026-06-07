package com.example.matefairy01.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.foundation.material.backgroundMaterial
import com.pico.spatial.ui.platform.Material
import kotlinx.coroutines.delay

@Composable
fun GameUIContainer(onClearMemory: () -> Unit = {}) {
    val textInputProvider = SharedUIManager.textInputProvider
    val voiceInputProvider = SharedUIManager.voiceInputProvider

    var showClearConfirm by remember { mutableStateOf(false) }

    // 10s 超时检测逻辑
    LaunchedEffect(textInputProvider.showInputDialog, textInputProvider.lastActiveTime) {
        if (textInputProvider.showInputDialog) {
            val remainingMs =
                (10_000 - (System.currentTimeMillis() - textInputProvider.lastActiveTime))
                    .coerceAtLeast(0)

            delay(remainingMs)

            if (
                textInputProvider.showInputDialog &&
                System.currentTimeMillis() - textInputProvider.lastActiveTime >= 10_000
            ) {
                textInputProvider.cancelInput()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (textInputProvider.showInputDialog) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(640.dp, 480.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    if (showClearConfirm) {
                        FairyDialogueUI.ConfirmDialog(
                            title = "清除所有记忆",
                            message = "将删除全部情景、事实与永久记忆（人设保留），此操作不可恢复。确认继续？",
                            confirmText = "确认清除",
                            cancelText = "取消",
                            onConfirm = {
                                showClearConfirm = false
                                onClearMemory()
                            },
                            onCancel = { showClearConfirm = false }
                        )
                    } else {
                        FairyDialogueUI.TextInputDialog(
                            text = textInputProvider.currentText,
                            onTextChange = { textInputProvider.updateText(it) },
                            onSubmit = {
                                textInputProvider.submitText()
                            },
                            onCancel = {
                                textInputProvider.cancelInput()
                            },
                            onClearMemory = {
                                android.util.Log.d("InputControllerManager", "clear memory button tapped")
                                showClearConfirm = true
                            }
                        )
                    }
                }

                // 悬浮在输入框下方的关闭按钮（毛玻璃材质）
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .backgroundMaterial(enable = true, style = Material.Regular)
                        .clickable { textInputProvider.cancelInput() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (voiceInputProvider.isListening()) {
            Box(
                modifier = Modifier
                    .size(200.dp, 200.dp)
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                FairyDialogueUI.RecordingIndicator(isRecording = true)
            }
        }
    }
}
