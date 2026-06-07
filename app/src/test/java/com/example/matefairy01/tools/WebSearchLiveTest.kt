package com.example.matefairy01.tools

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.matefairy01.config.AppConfigLoader
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * web_search 真实联网测试。
 *
 * - 从真实 assets/app_config.json 读取 webSearch 配置（默认 provider = duckduckgo，免 key）。
 * - 发真实 HTTP 请求，验证工具能跑通、返回非错误结果。
 * - 顺带打印「今天星期几」作为人工核对参照。
 *
 * 注意：依赖外网。无网络/被墙时会失败，属预期（这是联调而非纯单测）。
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class WebSearchLiveTest {

    @Test
    fun webSearch_realRequest_returnsResults() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<Application>()
        val config = AppConfigLoader.load(ctx)
        val wsConfig = config.webSearch

        println("==== web_search 配置 ====")
        println("enabled    = ${wsConfig.enabled}")
        println("provider   = ${wsConfig.provider}")
        println("apiKey     = ${if (wsConfig.apiKey.isBlank()) "<空，走免key兜底>" else "<已配置>"}")
        println("maxResults = ${wsConfig.maxResults}")

        // 本地算出真实星期几，作为人工核对参照
        val today = SimpleDateFormat("yyyy-MM-dd EEEE", Locale.CHINA).format(Date())
        println("==== 本地日期 ====")
        println("今天是: $today")

        val tool = WebSearchTool(wsConfig)
        val args = JSONObject().apply {
            put("query", "今天是几号 星期几")
        }

        val result = tool.execute(args)
        println("==== web_search 返回 ====")
        println(result)

        assertTrue("返回不应为空", result.isNotBlank())
        assertFalse("返回不应是错误: $result", result.startsWith("[ERROR]"))
        assertTrue("应包含搜索结果或明确无结果", result.contains("搜索结果") || result.contains("无结果"))
    }
}
