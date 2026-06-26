package com.example.matefairy01.orchestrator.decision

import com.example.matefairy01.animation.AnimationConfig
import com.example.matefairy01.interaction.LeaveChairActionController
import com.example.matefairy01.interaction.PlayFootballActionController
import com.example.matefairy01.interaction.PutDownRubberDuckActionController
import com.example.matefairy01.interaction.SqueezeRubberDuckActionController
import com.example.matefairy01.interaction.StartBoomboxActionController
import com.example.matefairy01.interaction.StayOnChairActionController
import com.example.matefairy01.interaction.StopBoomboxActionController

data class ActionIntentResolution(
    val actionIntent: String,
    val corrected: Boolean,
    val reason: String
)

object ActionIntentFallbackResolver {
    fun resolve(
        userText: String,
        replyText: String,
        modelActionIntent: String
    ): ActionIntentResolution {
        val normalizedModelIntent = modelActionIntent.trim().lowercase()
        val safeModelIntent = if (AnimationConfig.supportedActions.contains(normalizedModelIntent)) {
            normalizedModelIntent
        } else {
            NONE
        }
        val deterministic = inferFromText(userText, replyText)

        if (deterministic == null) {
            return ActionIntentResolution(
                actionIntent = safeModelIntent,
                corrected = safeModelIntent != normalizedModelIntent,
                reason = if (safeModelIntent == normalizedModelIntent) "model" else "unsupported-model-intent"
            )
        }

        val shouldOverride = safeModelIntent == NONE ||
            deterministic.priority >= priorityOf(safeModelIntent)

        return if (shouldOverride) {
            ActionIntentResolution(
                actionIntent = deterministic.intent,
                corrected = deterministic.intent != safeModelIntent,
                reason = deterministic.reason
            )
        } else {
            ActionIntentResolution(
                actionIntent = safeModelIntent,
                corrected = false,
                reason = "model-higher-priority"
            )
        }
    }

    private fun inferFromText(userText: String, replyText: String): DeterministicIntent? {
        val user = userText.compact()
        val reply = replyText.compact()
        val combined = "$user\n$reply"

        if (isLeaveChairIntent(combined)) {
            return DeterministicIntent(
                intent = LeaveChairActionController.ACTION_ID,
                priority = 100,
                reason = "chair-leave-text"
            )
        }
        if (isStayOnChairIntent(user) || hasChairCommitment(reply)) {
            return DeterministicIntent(
                intent = StayOnChairActionController.ACTION_ID,
                priority = 95,
                reason = "chair-stay-text"
            )
        }
        if (containsAny(combined, "踢球", "足球", "football", "kickfootball", "playfootball", "玩球")) {
            return DeterministicIntent(
                intent = PlayFootballActionController.ACTION_ID,
                priority = 90,
                reason = "football-text"
            )
        }
        if (containsAny(combined, "关闭音响", "停止音乐", "关掉音响", "关掉boombox", "stopboombox")) {
            return DeterministicIntent(
                intent = StopBoomboxActionController.ACTION_ID,
                priority = 85,
                reason = "stop-boombox-text"
            )
        }
        if (containsAny(combined, "打开音响", "启动音响", "播放音乐", "打开boombox", "startboombox")) {
            return DeterministicIntent(
                intent = StartBoomboxActionController.ACTION_ID,
                priority = 85,
                reason = "start-boombox-text"
            )
        }
        if (containsAny(combined, "放下小黄鸭", "放下鸭子", "把小黄鸭放下来", "把鸭子放下来")) {
            return DeterministicIntent(
                intent = PutDownRubberDuckActionController.ACTION_ID,
                priority = 80,
                reason = "put-down-duck-text"
            )
        }
        if (containsAny(combined, "捏小黄鸭", "挤小黄鸭", "让鸭子叫", "rubberduck", "捏鸭子")) {
            return DeterministicIntent(
                intent = SqueezeRubberDuckActionController.ACTION_ID,
                priority = 80,
                reason = "squeeze-duck-text"
            )
        }
        if (containsAny(combined, "跳舞", "dance")) {
            return DeterministicIntent(
                intent = "dance",
                priority = 60,
                reason = "dance-text"
            )
        }
        if (containsAny(combined, "迪斯科", "disco")) {
            return DeterministicIntent(
                intent = "disco",
                priority = 60,
                reason = "disco-text"
            )
        }
        return null
    }

    private fun isStayOnChairIntent(text: String): Boolean {
        if (!text.contains("椅子")) return false
        if (containsAny(text, "不要坐", "别坐", "不用坐", "不要去椅子", "别去椅子")) return false
        return containsAny(
            text,
            "去椅子",
            "到椅子",
            "坐到椅子",
            "坐在椅子",
            "椅子上坐",
            "椅子上待",
            "待在椅子",
            "飞到椅子",
            "椅子上休息",
            "找把椅子",
            "找个椅子",
            "坐下"
        )
    }

    private fun hasChairCommitment(reply: String): Boolean {
        if (!reply.contains("椅子")) return false
        if (!containsAny(reply, "我去", "我这就", "马上", "好的", "好呀", "去坐", "去待", "坐一会")) return false
        return containsAny(reply, "坐", "待", "休息", "过去", "飞过去")
    }

    private fun isLeaveChairIntent(text: String): Boolean {
        return containsAny(
            text,
            "离开椅子",
            "从椅子上下来",
            "椅子上下来",
            "别坐了",
            "不要坐",
            "别待在椅子",
            "不要待在椅子",
            "回来",
            "跟着我",
            "过来"
        )
    }

    private fun priorityOf(intent: String): Int {
        return when (intent) {
            LeaveChairActionController.ACTION_ID -> 100
            StayOnChairActionController.ACTION_ID -> 95
            PlayFootballActionController.ACTION_ID -> 90
            StartBoomboxActionController.ACTION_ID,
            StopBoomboxActionController.ACTION_ID -> 85
            SqueezeRubberDuckActionController.ACTION_ID,
            PutDownRubberDuckActionController.ACTION_ID -> 80
            "dance",
            "disco" -> 60
            "fetch_ball" -> 50
            else -> 0
        }
    }

    private data class DeterministicIntent(
        val intent: String,
        val priority: Int,
        val reason: String
    )

    private fun String.compact(): String {
        return trim()
            .lowercase()
            .replace(Regex("\\s+"), "")
            .replace("，", ",")
            .replace("。", ".")
            .replace("！", "!")
            .replace("？", "?")
    }

    private fun containsAny(text: String, vararg needles: String): Boolean {
        return needles.any { text.contains(it.compact()) }
    }

    private const val NONE = "none"
}
