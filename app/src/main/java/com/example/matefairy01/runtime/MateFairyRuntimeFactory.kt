package com.example.matefairy01.runtime

import android.content.Context
import com.example.matefairy01.action.ActionRegistry
import com.example.matefairy01.action.handlers.GenericAnimationHandler
import com.example.matefairy01.action.handlers.SceneInteractionActionHandler
import com.example.matefairy01.ai.LLMProviderFactory
import com.example.matefairy01.animation.AnimationConfig
import com.example.matefairy01.animation.AnimationModule
import com.example.matefairy01.audio.MusicModule
import com.example.matefairy01.avatar.DefaultAvatarController
import com.example.matefairy01.behavior.FairyFollowControlModule
import com.example.matefairy01.config.AppConfig
import com.example.matefairy01.emotion.AvatarEmotionRenderer
import com.example.matefairy01.emotion.EmotionEngine
import com.example.matefairy01.interaction.InteractionActionRuntimeDependencies
import com.example.matefairy01.interaction.PlayFootballActionController
import com.example.matefairy01.mcp.McpManager
import com.example.matefairy01.memory.ContextMemorySystem
import com.example.matefairy01.memory.db.DbProvider
import com.example.matefairy01.memory.episodic.EpisodicStore
import com.example.matefairy01.memory.ingestion.Consolidator
import com.example.matefairy01.memory.ingestion.ContradictionChecker
import com.example.matefairy01.memory.ingestion.FactExtractor
import com.example.matefairy01.memory.ingestion.IngestionWorker
import com.example.matefairy01.memory.ingestion.ShouldIngest
import com.example.matefairy01.memory.permanent.DreamJob
import com.example.matefairy01.memory.permanent.PermanentStore
import com.example.matefairy01.memory.retrieval.MemoryRetriever
import com.example.matefairy01.memory.retrieval.RecencyScorer
import com.example.matefairy01.memory.semantic.SemanticStore
import com.example.matefairy01.ml.EmbedderRemote
import com.example.matefairy01.ml.IEmbedder
import com.example.matefairy01.orchestrator.ConversationOrchestrator
import com.example.matefairy01.orchestrator.adapters.ActionRegistryPortAdapter
import com.example.matefairy01.orchestrator.adapters.EmotionEnginePortAdapter

/**
 * 集中组装当前应用运行时所需的核心依赖，替代散落在场景层和全局单例中的装配逻辑。
 *
 * 装配顺序（从无依赖到有依赖）：
 *   mcp/llm/embedder/db/scorer
 *     → stores (episodic/semantic)
 *     → retriever / permanentStore
 *     → ingestion (consolidator/factExtractor/contradictionChecker/worker)
 *     → dream
 *     → contextMemorySystem (注入 retriever + permanent)
 *     → conversationOrchestrator (注入 worker + dream)
 */
object MateFairyRuntimeFactory {

    fun create(context: Context, appConfig: AppConfig): MateFairyRuntime {
        // ----- 基础设施 -----
        val localTools = buildList {
            if (appConfig.webSearch.enabled) {
                add(com.example.matefairy01.tools.WebSearchTool(appConfig.webSearch))
            }
        }
        val mcpManager = McpManager(appConfig.mcpServers, localTools)
        val llmProvider = LLMProviderFactory.create(appConfig.ai, mcpManager)
        val animationModule = AnimationModule()
        InteractionActionRuntimeDependencies.lockState.addListener(animationModule)
        InteractionActionRuntimeDependencies.lockState.addListener(FairyFollowControlModule)
        val avatarController = DefaultAvatarController(animationModule)
        val musicModule = MusicModule(context)

        val emotionRenderer = AvatarEmotionRenderer(animationModule)
        val emotionEngine = EmotionEngine(emotionRenderer)
        InteractionActionRuntimeDependencies.actionRegistry.register(PlayFootballActionController())

        val actionRegistry = ActionRegistry().apply {
            AnimationConfig.supportedActions.filter { it != "none" }.forEach { intent ->
                register(GenericAnimationHandler(intent, animationModule))
            }
            register(
                SceneInteractionActionHandler(
                    intent = PlayFootballActionController.ACTION_ID,
                    requestBus = InteractionActionRuntimeDependencies.requestBus,
                    defaultObjectIds = listOf("football")
                )
            )
        }

        // ----- 记忆 ML / 持久层 -----
        val embedder: IEmbedder = EmbedderRemote(appConfig.memory.embedding)
        val database = DbProvider.get(context)
        val recencyScorer = RecencyScorer(appConfig.memory.retrieval.recencyHalfLifeDays)

        val episodicStore = EpisodicStore(
            dao = database.episodicDao(),
            embedder = embedder,
            memoryConfig = appConfig.memory,
            recencyScorer = recencyScorer
        )
        val semanticStore = SemanticStore(
            factDao = database.factDao(),
            tripleDao = database.tripleDao(),
            embedder = embedder,
            memoryConfig = appConfig.memory,
            recencyScorer = recencyScorer
        )

        val memoryRetriever = MemoryRetriever(episodicStore, semanticStore)
        val permanentStore = PermanentStore(context.applicationContext).also { it.ensureInitialized() }

        // ----- 写入流水线 -----
        val contradictionChecker = ContradictionChecker(llmProvider, semanticStore)
        val factExtractor = FactExtractor(llmProvider, semanticStore, contradictionChecker)
        val consolidator = Consolidator(llmProvider, episodicStore)
        val shouldIngest = ShouldIngest(appConfig.memory.factIngest)
        val ingestionWorker = IngestionWorker(
            consolidator = consolidator,
            factExtractor = factExtractor,
            shouldIngest = shouldIngest,
            ingestionConfig = appConfig.memory.ingestion
        )

        val dreamJob = DreamJob(
            llmProvider = llmProvider,
            episodicDao = database.episodicDao(),
            semanticStore = semanticStore,
            permanentStore = permanentStore,
            dreamConfig = appConfig.memory.dream
        )

        // ----- 上下文与编排 -----
        val contextMemorySystem = ContextMemorySystem(
            llmProvider = llmProvider,
            permanentStore = permanentStore,
            memoryRetriever = memoryRetriever
        )

        val conversationOrchestrator = ConversationOrchestrator(
            llmProvider = llmProvider,
            contextMemorySystem = contextMemorySystem,
            emotionPort = EmotionEnginePortAdapter(emotionEngine),
            actionPort = ActionRegistryPortAdapter(actionRegistry),
            ingestionWorker = ingestionWorker,
            dreamJob = dreamJob,
            consolidateConfig = appConfig.memory.consolidate,
            dreamConfig = appConfig.memory.dream
        )

        return MateFairyRuntime(
            conversationOrchestrator = conversationOrchestrator,
            avatarController = avatarController,
            animationModule = animationModule,
            mcpManager = mcpManager,
            embedder = embedder,
            database = database,
            episodicStore = episodicStore,
            semanticStore = semanticStore,
            memoryRetriever = memoryRetriever,
            ingestionWorker = ingestionWorker,
            permanentStore = permanentStore,
            dreamJob = dreamJob,
            musicModule = musicModule
        )
    }
}
