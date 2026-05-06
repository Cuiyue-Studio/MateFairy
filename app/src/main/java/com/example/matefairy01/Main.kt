package com.example.matefairy01

import com.example.matefairy01.content.HomeStage
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.DefaultStage
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope

fun mainApp(scope: SpatialAppScope) = with(scope) { DefaultStage { PicoTheme { HomeStage() } } }



