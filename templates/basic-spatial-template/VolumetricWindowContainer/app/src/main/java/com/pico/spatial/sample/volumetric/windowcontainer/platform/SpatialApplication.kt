package com.pico.spatial.sample.volumetric.windowcontainer.platform

import android.app.Application
import com.pico.spatial.ui.foundation.dsl.launch
import com.pico.spatial.sample.volumetric.windowcontainer.mainApp

class SpatialApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        launch(::mainApp)
    }
}
