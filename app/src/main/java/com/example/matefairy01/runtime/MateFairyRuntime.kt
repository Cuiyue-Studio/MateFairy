package com.example.matefairy01.runtime

import com.example.matefairy01.avatar.AvatarController
import com.example.matefairy01.orchestrator.ConversationOrchestrator

data class MateFairyRuntime(
    val conversationOrchestrator: ConversationOrchestrator,
    val avatarController: AvatarController
)
