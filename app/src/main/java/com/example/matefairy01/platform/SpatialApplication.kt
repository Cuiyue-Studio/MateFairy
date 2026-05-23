package com.example.matefairy01.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.example.matefairy01.mainApp
import com.example.matefairy01.ui.SharedUIManager

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SharedUIManager.initialize(this)
        launch(::mainApp)
    }
}
