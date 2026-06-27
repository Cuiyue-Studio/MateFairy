package com.example.matefairy01.persona

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FairySoulProfileMarkdownCodecTest {

    @Test
    fun markdown_roundTripsStructuredProfile() {
        val profile = FairySoulProfile(
            fairyName = "露米",
            userAddress = "小伙伴",
            personality = PersonalitySelections(
                extraversion = PersonalityLevel.HIGH,
                conscientiousness = PersonalityLevel.LOW,
                openness = PersonalityLevel.EXTREME,
                agreeableness = PersonalityLevel.MEDIUM,
                emotionalStability = PersonalityLevel.HIGH
            )
        )

        val markdown = FairySoulProfileMarkdownCodec.toMarkdown(profile)
        val decoded = FairySoulProfileMarkdownCodec.decode(markdown)

        assertEquals(profile.normalized(), decoded)
        assertTrue(markdown.contains("<!-- matefairy:soul-profile:v1 -->"))
        assertTrue(markdown.contains("精灵名称：露米"))
        assertTrue(markdown.contains("外向性：强（热情型）"))
    }

    @Test
    fun markdown_preservesLegacySoulContentBelowStructuredBlock() {
        val legacy = """
            # SOUL · 精灵人设

            ## 手工补充
            喜欢在玩家旁边飞行。
        """.trimIndent()

        val markdown = FairySoulProfileMarkdownCodec.toMarkdown(
            profile = FairySoulProfile(fairyName = "星星", userAddress = "队长"),
            previousMarkdown = legacy
        )

        assertTrue(markdown.contains("精灵名称：星星"))
        assertTrue(markdown.contains("## 手工补充"))
        assertTrue(markdown.contains("喜欢在玩家旁边飞行。"))
    }
}
