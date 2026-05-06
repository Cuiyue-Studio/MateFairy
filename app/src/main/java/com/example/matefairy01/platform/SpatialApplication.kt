package com.example.matefairy01.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.example.matefairy01.mainApp

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::mainApp)
    }
}
