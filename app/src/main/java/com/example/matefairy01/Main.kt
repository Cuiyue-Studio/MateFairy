package com.example.matefairy01

import androidx.compose.ui.unit.dp
import com.example.matefairy01.content.HomeStage
import com.example.matefairy01.ui.GameUIContainer
import com.pico.spatial.ui.foundation.dsl.Form
import com.pico.spatial.ui.design.PicoTheme
import com.pico.spatial.ui.foundation.dsl.DefaultStage
import com.pico.spatial.ui.foundation.dsl.SpatialAppScope
import com.pico.spatial.ui.foundation.dsl.WindowContainer
import com.pico.spatial.ui.foundation.dsl.WindowContainerSize
import com.pico.spatial.ui.platform.resize.CaptionBarType

fun mainApp(scope: SpatialAppScope) = with(scope) { 
    DefaultStage { 
        PicoTheme { 
            HomeStage() 
        } 
    } 

    // 全局系统级 UI 容器，用于渲染不受 3D 遮挡的用户级交互界面
    WindowContainer(
        id = "game_user_ui",
        form = Form.Planar,
        defaultSize = WindowContainerSize(width = 800.dp, height = 600.dp),
        enableMaterialBackground = false, // 依赖内部组件自身的毛玻璃，外部透明
        defaultCaptionBarType = CaptionBarType.AutomaticHide // 自动隐藏控制条
    ) {
        PicoTheme {
            GameUIContainer()
        }
    }
}



