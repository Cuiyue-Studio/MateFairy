package com.pico.spatial.sample.planar.windowcontainer.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.pico.spatial.sample.planar.windowcontainer.mainApp

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::mainApp)
    }
}
