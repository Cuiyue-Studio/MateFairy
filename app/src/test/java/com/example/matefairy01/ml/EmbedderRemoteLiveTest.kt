package com.example.matefairy01.ml

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.matefairy01.config.AppConfigLoader
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.math.sqrt

/**
 * 智谱 embedding-3 真实联网测试。
 *
 * - 从真实 app_config.json 读 memory.embedding（含已填的智谱 apiKey）。
 * - 验证非 stub 模式、warmup 成功、返回维度 == config.dimension(1024)。
 * - 验证语义相似度：相近文本 cosine 高，无关文本 cosine 低。
 *
 * 依赖外网 + 有效 key。无网/key 失效会失败，属预期。
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class EmbedderRemoteLiveTest {

    private fun cosine(a: FloatArray, b: FloatArray): Float {
        var dot = 0f; var na = 0f; var nb = 0f
        for (i in a.indices) { dot += a[i] * b[i]; na += a[i] * a[i]; nb += b[i] * b[i] }
        return if (na == 0f || nb == 0f) 0f else dot / (sqrt(na) * sqrt(nb))
    }

    @Test
    fun zhipu_embedding_live() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<Application>()
        val cfg = AppConfigLoader.load(ctx).memory.embedding

        println("==== embedding 配置 ====")
        println("provider  = ${cfg.provider}")
        println("baseUrl   = ${cfg.baseUrl}")
        println("model     = ${cfg.model}")
        println("dimension = ${cfg.dimension}")
        println("apiKey    = ${if (cfg.apiKey.isBlank()) "<空>" else "***" + cfg.apiKey.takeLast(4)}")

        val embedder = EmbedderRemote(cfg)
        val warm = embedder.warmup()
        println("warmup    = $warm (isReady=${embedder.isReady})")
        assertTrue("warmup 应成功（非 stub、维度对齐）", warm)

        val vA = embedder.encode("我喜欢喝拿铁咖啡")
        val vB = embedder.encode("我爱喝美式咖啡")
        val vC = embedder.encode("今天的篮球比赛很精彩")

        val nonZero = vA.any { it != 0f }
        println("vA 维度=${vA.size}, 非零=$nonZero, 前5=${vA.take(5)}")
        assertEquals("维度应等于配置值", cfg.dimension, vA.size)
        assertFalse("不应是零向量（即非 stub）", vA.all { it == 0f })

        val simClose = cosine(vA, vB)   // 都在讲喝咖啡，应高
        val simFar = cosine(vA, vC)     // 咖啡 vs 篮球，应低
        println("cosine(咖啡A, 咖啡B) = $simClose")
        println("cosine(咖啡A, 篮球C) = $simFar")
        assertTrue("相近文本相似度应高于无关文本", simClose > simFar)
    }
}
