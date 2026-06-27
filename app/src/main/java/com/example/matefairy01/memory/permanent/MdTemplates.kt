package com.example.matefairy01.memory.permanent

import com.example.matefairy01.persona.FairySoulProfile
import com.example.matefairy01.persona.FairySoulProfileMarkdownCodec

/**
 * L4 永久层三份 md 文件的初始模板。首次启动时由 [PermanentStore] 写到 filesDir/memory/。
 *
 * 设计原则：
 * - SOUL.md 是精灵人设，**Dream 不会改写**，由开发者初始定 + 用户设置面板/ADB 编辑
 * - USER.md 是用户画像，由 [DreamJob] 周期蒸馏自 [com.example.matefairy01.memory.semantic.Fact]
 * - MEMORY.md 是叙事记忆，由 [DreamJob] 周期蒸馏自 [com.example.matefairy01.memory.episodic.EpisodicEntry]
 */
object MdTemplates {

    val SOUL: String = FairySoulProfileMarkdownCodec.toMarkdown(FairySoulProfile.default())

    val USER: String = """
        # USER · 用户画像

        > 由 Dream 流程从 L3 facts 周期投影生成，**直接编辑会被下次 Dream 覆盖**。
        > 想稳定保留某条偏好，请在 ADB 里直接改 SOUL.md 或对话中明确表达。

        ## 偏好

        （首次启动时为空，随对话累计）

        ## 习惯

        （首次启动时为空，随对话累计）

        ## 身份信息

        （首次启动时为空，随对话累计）
    """.trimIndent()

    val MEMORY: String = """
        # MEMORY · 跨会话叙事记忆

        > 由 Dream 流程从 L2 episodic 摘要蒸馏生成。
        > 记录值得跨会话保留的事件、决定、共同经历。

        （首次启动时为空，随对话累计）
    """.trimIndent()

    const val FILE_SOUL = "SOUL.md"
    const val FILE_USER = "USER.md"
    const val FILE_MEMORY = "MEMORY.md"
    const val BACKUP_SUFFIX = ".bak"
}
