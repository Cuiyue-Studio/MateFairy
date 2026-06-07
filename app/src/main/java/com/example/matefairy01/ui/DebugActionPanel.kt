package com.example.matefairy01.ui

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matefairy01.interaction.InteractionActionRequest
import com.example.matefairy01.interaction.InteractionActionRuntimeDependencies
import com.example.matefairy01.interaction.InteractionActionSource
import com.example.matefairy01.interaction.StartBoomboxActionController
import com.example.matefairy01.interaction.StopBoomboxActionController
import com.pico.spatial.ui.design.Button
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.foundation.material.backgroundMaterial
import com.pico.spatial.ui.platform.Material

@Composable
fun DebugActionPanel() {
    var isBoomboxRequestedOn by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("用于调试：直接触发精灵打开音响 action") }

    Column(
        modifier = Modifier
            .width(360.dp)
            .clip(RoundedCornerShape(24.dp))
            .backgroundMaterial(enable = true, style = Material.Regular)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Debug Action",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = statusText,
            color = Color.White,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val nextEnabled = !isBoomboxRequestedOn
                val actionId = if (nextEnabled) {
                    StartBoomboxActionController.ACTION_ID
                } else {
                    StopBoomboxActionController.ACTION_ID
                }
                val didEnqueue = InteractionActionRuntimeDependencies.requestBus.enqueue(
                    InteractionActionRequest(
                        actionId = actionId,
                        objectIds = listOf(StartBoomboxActionController.DEFAULT_OBJECT_ID),
                        source = InteractionActionSource.DEBUG
                    )
                )
                if (didEnqueue) {
                    isBoomboxRequestedOn = nextEnabled
                    statusText = if (nextEnabled) {
                        "已触发：精灵打开音响"
                    } else {
                        "已触发：精灵关闭音响"
                    }
                } else {
                    statusText = "当前有 action 正在执行，请稍后再试"
                }
            }
        ) {
            Text(if (isBoomboxRequestedOn) "关闭音响" else "打开音响")
        }
    }
}
