package com.pico.spatial.sample.planar.windowcontainer.content

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pico.spatial.core.ecs.Entity
import com.pico.spatial.core.ecs.TransformComponent
import com.pico.spatial.core.ecs.resource.AssetBundle
import com.pico.spatial.core.math.EulerAngles
import com.pico.spatial.core.math.Vector3
import com.pico.spatial.sample.planar.windowcontainer.R
import com.pico.spatial.ui.design.Text
import com.pico.spatial.ui.foundation.content.SpatialView

@Composable
fun HomePage(modifier: Modifier) {
    Row(
        modifier.padding(64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.hello),
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333),
            fontSize = 64.sp,
            textAlign = TextAlign.Center
        )

        SpatialView(
            modifier = Modifier.size(360.dp),
            initial = { content, _ ->
                val model = Entity.loadSuspend(uriString = "asset://box.usdz").apply { content
                    components[TransformComponent::class.java]?.apply {
                        setEulerAngles(EulerAngles(90f, 0f, 0f))
                        setPosition(Vector3(0f, 0f, -.2f))
                    }
                }
                content.addEntity(model)
            }
        )
    }


}
