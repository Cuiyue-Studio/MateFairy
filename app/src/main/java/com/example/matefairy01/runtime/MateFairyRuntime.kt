package com.example.matefairy01.runtime

import com.example.matefairy01.animation.AnimationModule
import com.example.matefairy01.audio.MusicModule
import com.example.matefairy01.avatar.AvatarController
import com.example.matefairy01.mcp.McpManager
import com.example.matefairy01.orchestrator.ConversationOrchestrator

data class MateFairyRuntime(
    val conversationOrchestrator: ConversationOrchestrator,
    val avatarController: AvatarController,
    val animationModule: AnimationModule,
    val mcpManager: McpManager,
    val musicModule: MusicModule
)
