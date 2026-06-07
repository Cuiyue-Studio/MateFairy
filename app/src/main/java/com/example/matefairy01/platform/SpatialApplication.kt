package com.example.matefairy01.platform

import android.app.Application
import android.util.Log
import com.example.matefairy01.config.AppConfigLoader
import com.example.matefairy01.mainApp
import com.example.matefairy01.runtime.MateFairyRuntime
import com.example.matefairy01.runtime.MateFairyRuntimeFactory
import com.example.matefairy01.ui.SharedUIManager
import com.pico.spatial.ui.foundation.dsl.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch as ktLaunch

class SpatialApplication : Application() {

    companion object {
        private const val TAG = "SpatialApplication"
    }

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Application-scope 唯一 runtime 实例。
     * 通过 `(application as SpatialApplication).runtime` 在任意 Activity / Composable 中取用。
     */
    lateinit var runtime: MateFairyRuntime
        private set

    /** 安全访问标志：onPause 等生命周期回调时用，避免 [runtime] 未初始化崩溃 */
    val isRuntimeInitialized: Boolean
        get() = ::runtime.isInitialized

    override fun onCreate() {
        super.onCreate()
        SharedUIManager.initialize(this)

        val appConfig = AppConfigLoader.load(applicationContext)

        // 启动断言：embedder 维度配置错位会让 cosine 直接出错，宁可早崩
        val embeddingDim = appConfig.memory.embedding.dimension
        require(embeddingDim > 0) {
            "memory.embedding.dimension must be > 0, got=$embeddingDim"
        }

        runtime = MateFairyRuntimeFactory.create(applicationContext, appConfig)

        // Embedder 异步预热：网络往返 100-300ms，不阻塞 UI
        applicationScope.ktLaunch {
            val ok = runtime.embedder.warmup()
            Log.i(TAG, "Embedder warmup result: $ok (dim=${runtime.embedder.dimension})")
        }

        launch(::mainApp)
    }
}
