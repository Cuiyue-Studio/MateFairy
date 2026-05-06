package com.pico.spatial.sample.planar.windowcontainer

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.DefaultWindowContainer
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope
import com.pico.spatial.sample.planar.windowcontainer.content.HomePage

fun mainApp(scope: SpatialAppScope) =
    with(scope) {
        DefaultWindowContainer {
            PicoTheme {
                HomePage(Modifier.windowConstraints(width = 1080.dp, height = 640.dp))
            }
        }
    }
