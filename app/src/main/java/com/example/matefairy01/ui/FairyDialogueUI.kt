package com.example.matefairy01.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.design.TextField

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
        onCancel: () -> Unit
    ) {
        Box(
            modifier = Modifier
                .width(400.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xCC1A1A2E))
                .padding(24.dp)
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
                .width(300.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xE61A1A2E))
                .padding(16.dp)
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color.White,
                textAlign = TextAlign.Start,
                lineHeight = 20.sp,
                modifier = Modifier.fillMaxWidth()
            )
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
