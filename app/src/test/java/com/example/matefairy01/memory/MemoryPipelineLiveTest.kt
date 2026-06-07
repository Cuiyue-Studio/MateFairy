package com.example.matefairy01.memory

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.config.AppConfigLoader
import com.example.matefairy01.memory.db.MateFairyDatabase
import com.example.matefairy01.memory.episodic.EpisodicStore
import com.example.matefairy01.memory.ingestion.ContradictionChecker
import com.example.matefairy01.memory.ingestion.FactExtractor
import com.example.matefairy01.memory.retrieval.MemoryRetriever
import com.example.matefairy01.memory.retrieval.RecencyScorer
import com.example.matefairy01.memory.semantic.SemanticStore
import com.example.matefairy01.ml.EmbedderRemote
import com.example.matefairy01.testutil.ScriptedLLMProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * 四层记忆系统「真实 embedding」集成测试。
 *
 * 与 [MemoryPipelineTest] 同样的流程，但把 FakeEmbedder 换成真实 [EmbedderRemote]
 * （读 app_config.json 里的智谱 embedding-3 key），验证**真实语义向量**下记忆召回是否成立。
 *
 * 依赖外网 + 有效 key（无网/key 失效会失败，属预期）。
 * Room 仍用内存库，不落真机、不影响主项目。
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], application = Application::class)
class MemoryPipelineLiveTest {

    private lateinit var db: MateFairyDatabase
    private lateinit var embedder: EmbedderRemote
    private lateinit var memoryConfig: MemoryConfig
    private lateinit var recency: RecencyScorer
    private lateinit var episodicStore: EpisodicStore
    private lateinit var semanticStore: SemanticStore

    private val day = 24L * 3600L * 1000L

    @Before
    fun setup() = runBlocking {
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val appConfig = AppConfigLoader.load(ctx)
        memoryConfig = appConfig.memory

        embedder = EmbedderRemote(memoryConfig.embedding)
        val warm = embedder.warmup()
        println("embedder warmup = $warm, isReady=${embedder.isReady}, dim=${embedder.dimension}")
        assertTrue("真实 embedder 应连通（非 stub、维度对齐）", warm)

        db = Room.inMemoryDatabaseBuilder(ctx, MateFairyDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        recency = RecencyScorer(memoryConfig.retrieval.recencyHalfLifeDays)
        episodicStore = EpisodicStore(db.episodicDao(), embedder, memoryConfig, recency)
        semanticStore = SemanticStore(db.factDao(), db.tripleDao(), embedder, memoryConfig, recency)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun episodic_search_recalls_most_relevant() = runBlocking {
        val now = System.currentTimeMillis()
        episodicStore.add("用户喜欢喝美式咖啡", timestamp = now, importance = 5)
        episodicStore.add("用户在上海工作", timestamp = now - 10 * day, importance = 5)
        episodicStore.add("周末打算去登山", timestamp = now, importance = 5)

        val results = episodicStore.search("我想喝点咖啡", topK = 3)
        println("episodic 召回: ${results.map { it.item.content to it.similarity }}")

        assertTrue("应至少召回一条", results.isNotEmpty())
        assertEquals("最相关应为咖啡那条", "用户喜欢喝美式咖啡", results.first().item.content)
        assertTrue("咖啡那条相似度应 > 0", results.first().similarity > 0f)
    }

    @Test
    fun semantic_addFact_then_searchFacts_recalls() = runBlocking {
        semanticStore.addFact("preference", "用户对花生过敏", confidence = 0.9f)
        semanticStore.addFact("habit", "用户每天晚饭后散步", confidence = 0.8f)

        val results = semanticStore.searchFacts("有什么食物不能吃", topK = 5)
        println("semantic 召回: ${results.map { it.item.content to it.similarity }}")

        assertTrue(results.isNotEmpty())
        assertEquals("过敏事实应排第一", "用户对花生过敏", results.first().item.content)
    }

    @Test
    fun factExtractor_writes_facts_from_scripted_llm() = runBlocking {
        val llm = ScriptedLLMProvider(
            completeResponder = { systemPrompt, _ ->
                when {
                    systemPrompt.contains("事实抽取器") ->
                        """
                        {"facts":[
                          {"category":"preference","content":"用户喜欢美式咖啡","confidence":0.9},
                          {"category":"identity","content":"用户是程序员","confidence":0.8}
                        ]}
                        """.trimIndent()
                    else -> """{"supersededIds":[]}"""
                }
            }
        )
        val checker = ContradictionChecker(llm, semanticStore)
        val extractor = FactExtractor(llm, semanticStore, checker)

        val written = extractor.extract(
            listOf(
                ChatMessage("user", "我超爱喝美式咖啡，平时也写代码"),
                ChatMessage("assistant", "原来你是程序员呀")
            )
        )

        assertEquals("应写入 2 条事实", 2, written.size)
        assertEquals(2, semanticStore.countActiveFacts())
    }

    @Test
    fun contradictionChecker_supersedes_old_fact() = runBlocking {
        val oldId = semanticStore.addFact("identity", "用户在上海工作", confidence = 0.9f)
        val newId = semanticStore.addFact("identity", "用户在北京工作", confidence = 0.9f)

        val llm = ScriptedLLMProvider(
            completeResponder = { _, _ -> """{"supersededIds":[$oldId]}""" }
        )
        val checker = ContradictionChecker(llm, semanticStore)

        val applied = checker.checkAndSupersede(newId, "identity", "用户在北京工作")

        assertEquals(listOf(oldId), applied)
        assertEquals("旧事实应被标记 supersededBy=新id", newId, semanticStore.findFact(oldId)!!.supersededBy)
        assertEquals("活跃事实只剩 1 条", 1, semanticStore.countActiveFacts())
    }

    @Test
    fun memoryRetriever_assembles_facts_and_episodes() = runBlocking {
        val now = System.currentTimeMillis()
        semanticStore.addFact("preference", "用户喜欢喝美式咖啡", confidence = 0.9f)
        episodicStore.add("我们讨论了几家精品咖啡店", timestamp = now, importance = 6)

        val retriever = MemoryRetriever(episodicStore, semanticStore)
        val context = retriever.assembleContext("聊聊咖啡吧")
        println("assembled context:\n$context")

        assertNotNull(context)
        assertTrue("应含事实段标题", context.contains("# 关于这位用户"))
        assertTrue("应含情节段标题", context.contains("# 过往会话片段"))
        assertTrue("应召回咖啡相关内容", context.contains("咖啡"))
    }
}
