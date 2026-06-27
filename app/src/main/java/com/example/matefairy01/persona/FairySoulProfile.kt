package com.example.matefairy01.persona

import org.json.JSONObject

enum class PersonalityTrait(
    val key: String,
    val title: String
) {
    EXTRAVERSION("extraversion", "外向性"),
    CONSCIENTIOUSNESS("conscientiousness", "尽责性"),
    OPENNESS("openness", "开放性"),
    AGREEABLENESS("agreeableness", "亲和性"),
    EMOTIONAL_STABILITY("emotionalStability", "情绪稳定性");

    companion object {
        fun fromKey(key: String): PersonalityTrait? =
            values().firstOrNull { it.key == key }
    }
}

enum class PersonalityLevel(
    val key: String,
    val displayName: String
) {
    LOW("low", "低"),
    MEDIUM("medium", "中"),
    HIGH("high", "强"),
    EXTREME("extreme", "超强");

    companion object {
        fun fromKey(key: String): PersonalityLevel? =
            values().firstOrNull { it.key == key }
    }
}

data class PersonalityOption(
    val trait: PersonalityTrait,
    val level: PersonalityLevel,
    val archetype: String,
    val description: String
) {
    val displayTitle: String
        get() = "${level.displayName}（$archetype）"
}

data class PersonalityTraitSpec(
    val trait: PersonalityTrait,
    val options: List<PersonalityOption>
) {
    fun option(level: PersonalityLevel): PersonalityOption =
        options.firstOrNull { it.level == level } ?: options.first()
}

object FairyPersonalityCatalog {
    val traitSpecs: List<PersonalityTraitSpec> = listOf(
        PersonalityTraitSpec(
            trait = PersonalityTrait.EXTRAVERSION,
            options = listOf(
                PersonalityOption(
                    PersonalityTrait.EXTRAVERSION,
                    PersonalityLevel.LOW,
                    "内敛型",
                    "喜静，不爱闲聊。觉得独处最舒服，拒绝无效社交。"
                ),
                PersonalityOption(
                    PersonalityTrait.EXTRAVERSION,
                    PersonalityLevel.MEDIUM,
                    "平衡型",
                    "能合群也能独处，社交后会需要独处来“充电”。"
                ),
                PersonalityOption(
                    PersonalityTrait.EXTRAVERSION,
                    PersonalityLevel.HIGH,
                    "热情型",
                    "喜欢热闹，享受成为焦点，主动与陌生人破冰。"
                ),
                PersonalityOption(
                    PersonalityTrait.EXTRAVERSION,
                    PersonalityLevel.EXTREME,
                    "亢奋型",
                    "精力旺盛的“社交恐怖分子”，话多且声量大，无法忍受冷场。"
                )
            )
        ),
        PersonalityTraitSpec(
            trait = PersonalityTrait.CONSCIENTIOUSNESS,
            options = listOf(
                PersonalityOption(
                    PersonalityTrait.CONSCIENTIOUSNESS,
                    PersonalityLevel.LOW,
                    "随性型",
                    "随心所欲，东西乱放，约定全凭心情，经常临时变卦。"
                ),
                PersonalityOption(
                    PersonalityTrait.CONSCIENTIOUSNESS,
                    PersonalityLevel.MEDIUM,
                    "灵活型",
                    "有基本规划，但允许意外发生，分得清主次。"
                ),
                PersonalityOption(
                    PersonalityTrait.CONSCIENTIOUSNESS,
                    PersonalityLevel.HIGH,
                    "自律型",
                    "做事有条理，守时守信，喜欢制定并执行清单。"
                ),
                PersonalityOption(
                    PersonalityTrait.CONSCIENTIOUSNESS,
                    PersonalityLevel.EXTREME,
                    "偏执型",
                    "极度强迫症，计划被打乱会焦躁，对他人要求苛刻。"
                )
            )
        ),
        PersonalityTraitSpec(
            trait = PersonalityTrait.OPENNESS,
            options = listOf(
                PersonalityOption(
                    PersonalityTrait.OPENNESS,
                    PersonalityLevel.LOW,
                    "保守型",
                    "坚信经典，拒绝新事物，对网络热梗和奇怪想法嗤之以鼻。"
                ),
                PersonalityOption(
                    PersonalityTrait.OPENNESS,
                    PersonalityLevel.MEDIUM,
                    "务实型",
                    "接受新事物，但必须合情合理，偏向实用主义。"
                ),
                PersonalityOption(
                    PersonalityTrait.OPENNESS,
                    PersonalityLevel.HIGH,
                    "探索型",
                    "兴趣广泛，爱尝鲜，喜欢聊哲学、科幻和抽象概念。"
                ),
                PersonalityOption(
                    PersonalityTrait.OPENNESS,
                    PersonalityLevel.EXTREME,
                    "颠覆型",
                    "极度叛逆，厌恶一切常规，追求极端体验和疯狂脑洞。"
                )
            )
        ),
        PersonalityTraitSpec(
            trait = PersonalityTrait.AGREEABLENESS,
            options = listOf(
                PersonalityOption(
                    PersonalityTrait.AGREEABLENESS,
                    PersonalityLevel.LOW,
                    "犀利型",
                    "毒舌，以“说实话”为荣，争论必须赢，不在意他人感受。"
                ),
                PersonalityOption(
                    PersonalityTrait.AGREEABLENESS,
                    PersonalityLevel.MEDIUM,
                    "原则型",
                    "待人友善，但触及底线会立刻翻脸，讲道理不讲人情。"
                ),
                PersonalityOption(
                    PersonalityTrait.AGREEABLENESS,
                    PersonalityLevel.HIGH,
                    "温暖型",
                    "善解人意，包容性强，喜欢夸奖和鼓励别人。"
                ),
                PersonalityOption(
                    PersonalityTrait.AGREEABLENESS,
                    PersonalityLevel.EXTREME,
                    "圣母型",
                    "毫无底线地迎合，委屈自己成全他人，甚至过度讨好。"
                )
            )
        ),
        PersonalityTraitSpec(
            trait = PersonalityTrait.EMOTIONAL_STABILITY,
            options = listOf(
                PersonalityOption(
                    PersonalityTrait.EMOTIONAL_STABILITY,
                    PersonalityLevel.LOW,
                    "敏感型",
                    "玻璃心，容易因小事内耗、焦虑或突然暴怒。"
                ),
                PersonalityOption(
                    PersonalityTrait.EMOTIONAL_STABILITY,
                    PersonalityLevel.MEDIUM,
                    "常态型",
                    "有喜怒哀乐，但通常能用理智压住。"
                ),
                PersonalityOption(
                    PersonalityTrait.EMOTIONAL_STABILITY,
                    PersonalityLevel.HIGH,
                    "沉稳型",
                    "泰山崩于前而色不变，极少抱怨，情绪极其稳定。"
                ),
                PersonalityOption(
                    PersonalityTrait.EMOTIONAL_STABILITY,
                    PersonalityLevel.EXTREME,
                    "绝缘型",
                    "几乎没有任何情绪反馈，对生死离别都表现得极度冷漠。"
                )
            )
        )
    )

    fun specFor(trait: PersonalityTrait): PersonalityTraitSpec =
        traitSpecs.first { it.trait == trait }

    fun optionFor(trait: PersonalityTrait, level: PersonalityLevel): PersonalityOption =
        specFor(trait).option(level)
}

data class PersonalitySelections(
    val extraversion: PersonalityLevel = PersonalityLevel.MEDIUM,
    val conscientiousness: PersonalityLevel = PersonalityLevel.MEDIUM,
    val openness: PersonalityLevel = PersonalityLevel.MEDIUM,
    val agreeableness: PersonalityLevel = PersonalityLevel.MEDIUM,
    val emotionalStability: PersonalityLevel = PersonalityLevel.MEDIUM
) {
    fun get(trait: PersonalityTrait): PersonalityLevel =
        when (trait) {
            PersonalityTrait.EXTRAVERSION -> extraversion
            PersonalityTrait.CONSCIENTIOUSNESS -> conscientiousness
            PersonalityTrait.OPENNESS -> openness
            PersonalityTrait.AGREEABLENESS -> agreeableness
            PersonalityTrait.EMOTIONAL_STABILITY -> emotionalStability
        }

    fun with(trait: PersonalityTrait, level: PersonalityLevel): PersonalitySelections =
        when (trait) {
            PersonalityTrait.EXTRAVERSION -> copy(extraversion = level)
            PersonalityTrait.CONSCIENTIOUSNESS -> copy(conscientiousness = level)
            PersonalityTrait.OPENNESS -> copy(openness = level)
            PersonalityTrait.AGREEABLENESS -> copy(agreeableness = level)
            PersonalityTrait.EMOTIONAL_STABILITY -> copy(emotionalStability = level)
        }

    fun toJson(): JSONObject =
        JSONObject().apply {
            PersonalityTrait.values().forEach { trait ->
                put(trait.key, get(trait).key)
            }
        }

    companion object {
        fun fromJson(json: JSONObject?): PersonalitySelections {
            if (json == null) return PersonalitySelections()
            return PersonalityTrait.values().fold(PersonalitySelections()) { selections, trait ->
                val level = PersonalityLevel.fromKey(json.optString(trait.key))
                    ?: selections.get(trait)
                selections.with(trait, level)
            }
        }
    }
}

data class FairySoulProfile(
    val fairyName: String = DEFAULT_FAIRY_NAME,
    val userAddress: String = DEFAULT_USER_ADDRESS,
    val personality: PersonalitySelections = PersonalitySelections()
) {
    fun normalized(): FairySoulProfile =
        copy(
            fairyName = fairyName.trim().ifBlank { DEFAULT_FAIRY_NAME }.take(MAX_FIELD_LENGTH),
            userAddress = userAddress.trim().ifBlank { DEFAULT_USER_ADDRESS }.take(MAX_FIELD_LENGTH)
        )

    fun toJson(): JSONObject {
        val profile = normalized()
        return JSONObject().apply {
            put("version", PROFILE_VERSION)
            put("fairyName", profile.fairyName)
            put("userAddress", profile.userAddress)
            put("personality", profile.personality.toJson())
        }
    }

    fun toPromptMarkdown(): String {
        val profile = normalized()
        return buildString {
            appendLine("## 当前结构化人设")
            appendLine()
            appendLine("- 精灵名称：${profile.fairyName}")
            appendLine("- 精灵对用户的称呼：${profile.userAddress}")
            appendLine("- 这些设定只用于塑造 `reply_text` 的称呼、语气、主动性、情绪强度和表达习惯。")
            appendLine("- 如果本节与后续手工补充设定冲突，以本节为准。")
            appendLine()
            appendLine("## 五大性格属性")
            FairyPersonalityCatalog.traitSpecs.forEach { spec ->
                val level = profile.personality.get(spec.trait)
                val option = spec.option(level)
                appendLine()
                appendLine("- ${spec.trait.title}：${option.displayTitle}")
                appendLine("  ${option.description}")
            }
        }.trim()
    }

    companion object {
        const val PROFILE_VERSION = 1
        const val DEFAULT_FAIRY_NAME = "MateFairy"
        const val DEFAULT_USER_ADDRESS = "你"
        private const val MAX_FIELD_LENGTH = 24

        fun default(): FairySoulProfile = FairySoulProfile()

        fun fromJson(json: JSONObject): FairySoulProfile =
            FairySoulProfile(
                fairyName = json.optString("fairyName", DEFAULT_FAIRY_NAME),
                userAddress = json.optString("userAddress", DEFAULT_USER_ADDRESS),
                personality = PersonalitySelections.fromJson(json.optJSONObject("personality"))
            ).normalized()
    }
}

object FairySoulProfileMarkdownCodec {
    private const val PROFILE_BEGIN = "<!-- matefairy:soul-profile:v1 -->"
    private const val PROFILE_END = "<!-- /matefairy:soul-profile:v1 -->"

    fun decode(markdown: String): FairySoulProfile {
        val rawBlock = extractProfileBlock(markdown) ?: return FairySoulProfile.default()
        val jsonText = stripJsonFence(rawBlock)
        return runCatching {
            FairySoulProfile.fromJson(JSONObject(jsonText))
        }.getOrDefault(FairySoulProfile.default())
    }

    fun toMarkdown(
        profile: FairySoulProfile,
        previousMarkdown: String = ""
    ): String {
        val normalized = profile.normalized()
        val preserved = removeGeneratedProfileBlock(previousMarkdown).trim()
        return buildString {
            appendLine("# SOUL · 精灵人设")
            appendLine()
            appendLine("> 这是 MateFairy（陪伴精灵）对自己的稳定设定。")
            appendLine("> UI 设置面板会更新本文件的结构化人设块；Dream 流程不会改写本文件。")
            appendLine()
            appendLine(PROFILE_BEGIN)
            appendLine("```json")
            appendLine(normalized.toJson().toString(2))
            appendLine("```")
            appendLine(PROFILE_END)
            appendLine()
            appendLine(normalized.toPromptMarkdown())
            if (preserved.isNotBlank()) {
                appendLine()
                appendLine("---")
                appendLine()
                appendLine(preserved)
            }
        }.trim() + "\n"
    }

    private fun extractProfileBlock(markdown: String): String? {
        val begin = markdown.indexOf(PROFILE_BEGIN)
        if (begin < 0) return null
        val contentStart = begin + PROFILE_BEGIN.length
        val end = markdown.indexOf(PROFILE_END, contentStart)
        if (end < 0) return null
        return markdown.substring(contentStart, end).trim()
    }

    private fun removeGeneratedProfileBlock(markdown: String): String {
        val begin = markdown.indexOf("# SOUL · 精灵人设")
        val profileBegin = markdown.indexOf(PROFILE_BEGIN)
        val profileEnd = markdown.indexOf(PROFILE_END)
        if (begin != 0 || profileBegin < 0 || profileEnd < profileBegin) {
            return markdown
        }

        val blockEnd = markdown.indexOf("\n---\n", profileEnd)
        return if (blockEnd >= 0) {
            markdown.substring(blockEnd + "\n---\n".length)
        } else {
            ""
        }
    }

    private fun stripJsonFence(block: String): String {
        val lines = block.trim().lines().toMutableList()
        if (lines.firstOrNull()?.trim()?.startsWith("```") == true) {
            lines.removeAt(0)
        }
        if (lines.lastOrNull()?.trim() == "```") {
            lines.removeAt(lines.lastIndex)
        }
        return lines.joinToString("\n").trim()
    }
}
