package com.example.matefairy01.platform

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import com.example.matefairy01.config.AppConfigLoader
import com.pico.spatial.ui.foundation.dsl.launch
import com.example.matefairy01.mainApp
import com.example.matefairy01.ui.SharedUIManager

class SpatialApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        SharedUIManager.initialize(this)
        applicationScope.launch {
            AppConfigLoader.load(applicationContext)
        }
        launch(::mainApp)
    }
}
