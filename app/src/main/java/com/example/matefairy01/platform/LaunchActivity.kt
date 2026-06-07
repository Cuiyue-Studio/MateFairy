package com.example.matefairy01.platform

import android.util.Log
import com.pico.spatial.ui.platform.stub.SpatialLaunchActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class LaunchActivity : SpatialLaunchActivity() {

    companion object {
        private const val TAG = "LaunchActivity"
    }

    /**
     * 不用 lifecycleScope：onPause 后 Activity 进 Cached App 阶段会取消 lifecycleScope，
     * flush 协程跑一半就被砍。改用 application-scope 的独立 SupervisorScope，
     * 让记忆 flush 在用户摘下头显后仍有 ~15s 窗口（系统真正杀进程之前）跑完任务。
     */
    private val flushScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onPause() {
        super.onPause()
        val app = (application as? SpatialApplication)
        if (app == null || !app.isRuntimeInitialized) {
            Log.d(TAG, "onPause: runtime not ready, skip flush")
            return
        }
        flushScope.launch {
            runCatching {
                Log.d(TAG, "onPause: flushing ingestion worker...")
                app.runtime.conversationOrchestrator.flushOnPause()
                Log.d(TAG, "onPause: flush done")
            }.onFailure {
                Log.w(TAG, "onPause flush failed: ${it.message}", it)
            }
        }
    }
}
