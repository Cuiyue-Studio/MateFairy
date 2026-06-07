package com.example.matefairy01.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.design.TextField
import com.pico.spatial.ui.foundation.material.backgroundMaterial
import com.pico.spatial.ui.platform.Material

/**
 * 精灵对话 UI 组件
 * 包含文本输入框和对话气泡显示
 */
object FairyDialogueUI {

    /**
     * 文本输入对话框
     */
    @Composable
    fun TextInputDialog(
        text: String,
        onTextChange: (String) -> Unit,
        onSubmit: () -> Unit,
        onCancel: () -> Unit,
        onClearMemory: () -> Unit = {}
    ) {
        Box(
            modifier = Modifier
                .width(600.dp)
                .clip(RoundedCornerShape(32.dp))
                .backgroundMaterial(
                    enable = true,
                    style = Material.Regular
                )
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "和精灵对话",
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = { Text("输入你想说的话...") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = false,
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = onClearMemory) {
                            Text("清除记忆")
                        }
                        Button(
                            onClick = onSubmit,
                            enabled = text.isNotBlank()
                        ) {
                            Text("发送")
                        }
                    }
                }
            }
        }
    }

    /**
     * 对话气泡组件
     */
    @Composable
    fun DialogueBubble(
        text: String,
        modifier: Modifier = Modifier
    ) {
        if (text.isBlank()) return

        Box(
            modifier = modifier
                .width(520.dp)
                .clip(RoundedCornerShape(20.dp))
                .backgroundMaterial(
                    enable = true,
                    style = Material.Regular
                )
                .padding(horizontal = 28.dp, vertical = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4E342E),
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    /**
     * 通用确认弹窗（用于危险操作二次确认，如清除记忆）
     */
    @Composable
    fun ConfirmDialog(
        title: String,
        message: String,
        confirmText: String = "确认",
        cancelText: String = "取消",
        onConfirm: () -> Unit,
        onCancel: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .width(520.dp)
                .clip(RoundedCornerShape(32.dp))
                .backgroundMaterial(enable = true, style = Material.Regular)
                .padding(32.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(onClick = onCancel) {
                        Text(cancelText)
                    }
                    Button(onClick = onConfirm) {
                        Text(confirmText)
                    }
                }
            }
        }
    }

    /**
     * 录音状态指示器
     */
    @Composable
    fun RecordingIndicator(
        isRecording: Boolean
    ) {
        if (!isRecording) return

        Box(
            modifier = Modifier
                .width(200.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xE6FF4444))
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "正在录音...",
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
