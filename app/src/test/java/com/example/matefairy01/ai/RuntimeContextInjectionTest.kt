package com.example.matefairy01.ai

import android.app.Application
import com.example.matefairy01.config.DeepSeekConfig
import org.json.JSONArray
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 验证「当前时间注入」逻辑（仿 NAVI runtime-context）。
 *
 * 不连网、不发请求：直接反射调用 private buildToolModeMessagesJson，
 * 断言最后一条 user 消息被注入了 <runtime-context> + 实时星期。
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class RuntimeContextInjectionTest {

    @Test
    fun lastUserMessage_getsRuntimeTimeInjected() {
        val provider = DeepSeekLLMProvider(DeepSeekConfig(), "你是空间精灵。", null)

        val method: Method = DeepSeekLLMProvider::class.java
            .getDeclaredMethod("buildToolModeMessagesJson", List::class.java)
            .apply { isAccessible = true }

        val messages = listOf(
            ChatMessage(role = "system", content = "你是空间精灵。"),
            ChatMessage(role = "user", content = "你好"),
            ChatMessage(role = "assistant", content = "你好呀"),
            ChatMessage(role = "user", content = "今天星期几？")
        )

        val arr = method.invoke(provider, messages) as JSONArray
        val last = arr.getJSONObject(arr.length() - 1)
        val content = last.getString("content")

        println("==== 注入后的最后一条 user 消息 ====")
        println(content)

        val weekday = SimpleDateFormat("EEEE", Locale.CHINA).format(Date())
        assertTrue("应含 runtime-context 块", content.contains("<runtime-context>"))
        assertTrue("应含 Current Time", content.contains("Current Time:"))
        assertTrue("应含今天实时星期 [$weekday]", content.contains(weekday))
        assertTrue("应保留原始用户问题", content.contains("今天星期几？"))

        // 早一条 user（index 1）不应被注入，只注入最后一条
        val firstUser = arr.getJSONObject(1).getString("content")
        assertTrue("旧 user 消息不该被注入", !firstUser.contains("<runtime-context>"))
    }
}
