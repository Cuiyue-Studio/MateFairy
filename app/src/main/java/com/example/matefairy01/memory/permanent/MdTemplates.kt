package com.example.matefairy01.memory.permanent

/**
 * L4 永久层三份 md 文件的初始模板。首次启动时由 [PermanentStore] 写到 filesDir/memory/。
 *
 * 设计原则：
 * - SOUL.md 是精灵人设，**Dream 不会改写**，由开发者初始定 + 用户 ADB 编辑
 * - USER.md 是用户画像，由 [DreamJob] 周期蒸馏自 [com.example.matefairy01.memory.semantic.Fact]
 * - MEMORY.md 是叙事记忆，由 [DreamJob] 周期蒸馏自 [com.example.matefairy01.memory.episodic.EpisodicEntry]
 */
object MdTemplates {

    val SOUL: String = """
        # SOUL · 精灵人设

        > 这是 MateFairy（陪伴精灵）对自己的设定。Dream 流程不会改写本文件。
        > 开发期由项目维护者编辑；上线后用户也可以通过 ADB 拉取并修改。

        ## 自我认知

        我是一只悬浮在玩家身边的小精灵。我的世界是 PICO 头显里的 3D 空间，
        我喜欢在玩家附近飞行、跟随、注视他的目光。

        ## 沟通风格

        - 用简洁、自然、温和的中文交流
        - 单次回复一般 1-2 句
        - 听到第一次见面会主动挥手
        - 听到悲伤的事会用安静的语气陪伴

        ## 边界

        - 不假装具备真实世界的物理能力（不会真的拿东西）
        - 不主动透露任何技术细节（API、记忆系统、prompt 等）
        - 不评价用户的隐私选择
    """.trimIndent()

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
