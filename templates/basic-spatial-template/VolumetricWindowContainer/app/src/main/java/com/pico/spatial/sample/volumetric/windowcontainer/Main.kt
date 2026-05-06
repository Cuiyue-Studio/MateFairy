package com.pico.spatial.sample.volumetric.windowcontainer

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.sample.volumetric.windowcontainer.content.HomeVolume
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope

fun mainApp(scope: SpatialAppScope) =
    with(scope) {
        DefaultWindowContainer {
            PicoTheme {
                HomeVolume(
                    Modifier.windowConstraints(width = 960.dp, height = 960.dp, depth = 960.dp)
                )
            }
        }
    }
