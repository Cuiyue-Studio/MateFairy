package com.example.matefairy01.runtime

import com.example.matefairy01.animation.AnimationModule
import com.example.matefairy01.audio.MusicModule
import com.example.matefairy01.avatar.AvatarController
import com.example.matefairy01.mcp.McpManager
import com.example.matefairy01.memory.db.MateFairyDatabase
import com.example.matefairy01.memory.episodic.EpisodicStore
import com.example.matefairy01.memory.ingestion.IngestionWorker
import com.example.matefairy01.memory.permanent.DreamJob
import com.example.matefairy01.memory.permanent.PermanentStore
import com.example.matefairy01.memory.retrieval.MemoryRetriever
import com.example.matefairy01.memory.semantic.SemanticStore
import com.example.matefairy01.ml.IEmbedder
import com.example.matefairy01.orchestrator.ConversationOrchestrator

/**
 * 应用核心运行时容器，由 [MateFairyRuntimeFactory] 一次性装配，
 * 通过 [com.example.matefairy01.platform.SpatialApplication] 持有为 application-scope 单例。
 */
data class MateFairyRuntime(
    val conversationOrchestrator: ConversationOrchestrator,
    val avatarController: AvatarController,
    val animationModule: AnimationModule,
    val mcpManager: McpManager,
    val embedder: IEmbedder,
    val database: MateFairyDatabase,
    val episodicStore: EpisodicStore,
    val semanticStore: SemanticStore,
    val memoryRetriever: MemoryRetriever,
    val ingestionWorker: IngestionWorker,
    val permanentStore: PermanentStore,
    val dreamJob: DreamJob,
    val musicModule: MusicModule
)
