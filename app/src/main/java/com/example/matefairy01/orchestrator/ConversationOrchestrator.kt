package com.example.matefairy01.orchestrator

import android.util.Log
import com.example.matefairy01.ai.ChatMessage
import com.example.matefairy01.ai.ILLMProvider
import com.example.matefairy01.memory.ContextMemorySystem
import com.example.matefairy01.memory.ConsolidateConfig
import com.example.matefairy01.memory.DreamConfig
import com.example.matefairy01.memory.ingestion.IngestionJob
import com.example.matefairy01.memory.ingestion.IngestionWorker
import com.example.matefairy01.memory.permanent.DreamJob
import com.example.matefairy01.orchestrator.decision.BehaviorDecisionMaker
import com.example.matefairy01.orchestrator.decision.DefaultBehaviorDecisionMaker
import com.example.matefairy01.orchestrator.ports.ActionCommandPort
import com.example.matefairy01.orchestrator.ports.EmotionCommandPort
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ConversationResult(
    val replyText: String
)

/**
 * 统一封装"用户输入 -> 上下文 -> LLM -> 情绪/动作分发"的编排逻辑。
 *
 * P1.5 起新增旁路：
 * - 调用 [ContextMemorySystem.buildPromptMessages] 注入 SOUL/USER/recall
 * - afterReply hook：异步入队 ConsolidateJob + ExtractFactsJob，主对话不等
 * - 空闲计时器：最后一次互动后 [DreamConfig.idleMs] 触发 Dream
 *
 * 全部新组件**可选注入**：构造时不传依然兼容旧行为（仅在迁移期使用）。
 */
class ConversationOrchestrator(
    private val llmProvider: ILLMProvider,
    private val contextMemorySystem: ContextMemorySystem,
    private val emotionPort: EmotionCommandPort,
    private val actionPort: ActionCommandPort,
    private val ingestionWorker: IngestionWorker? = null,
    private val dreamJob: DreamJob? = null,
    private val consolidateConfig: ConsolidateConfig = ConsolidateConfig(),
    private val dreamConfig: DreamConfig = DreamConfig(),
    private val decisionMaker: BehaviorDecisionMaker = DefaultBehaviorDecisionMaker()
) {
    companion object {
        private const val TAG = "ConvOrchestrator"
    }

    private val backgroundScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Volatile
    private var idleDreamJob: Job? = null

    @Volatile
    private var turnCounter: Int = 0

    @Volatile
    private var accumulatedTokens: Int = 0

    suspend fun processUserInput(text: String): ConversationResult {
        contextMemorySystem.addMessage(ChatMessage(role = "user", content = text))

        // 改造点：注入 SOUL/USER/recall 到 prompt
        val messages = contextMemorySystem.buildPromptMessages(query = text)
        val response = llmProvider.chat(messages)

        contextMemorySystem.addMessage(
            ChatMessage(role = "assistant", content = response.reply_text)
        )

        // 3. 决策优先级拦截 (动作 vs 情绪)
        val decision = decisionMaker.decide(
            emotion = response.emotion,
            actionIntent = response.action_intent,
            originalReply = response.reply_text
        )

        // 4. 分发执行
        if (decision.shouldTriggerEmotion) {
            // 情绪分发是同步非阻塞的，直接调用
            emotionPort.triggerEmotion(decision.resolvedEmotion)
        }

        if (decision.shouldDispatchAction) {
            // 采用 coroutineScope 启动子协程分发动作，避免长动画挂起阻塞当前函数返回，
            // 从而让 reply_text 能够立刻抛出并显示给用户。
            coroutineScope {
                launch {
                    actionPort.dispatchAction(decision.resolvedActionIntent)
                }
            }
        }

        // 5. afterReply hook：旁路异步任务，主对话不等
        afterReply(userText = text, replyText = response.reply_text)

        return ConversationResult(replyText = decision.overriddenReplyText ?: response.reply_text)
    }

    /**
     * 主对话已经返回给 UI 之后调用。所有耗时操作（LLM 抽事实 / 蒸馏）走 [IngestionWorker]。
     */
    private fun afterReply(userText: String, replyText: String) {
        turnCounter += 1
        accumulatedTokens += estimateTokens(userText) + estimateTokens(replyText)

        val worker = ingestionWorker ?: return

        // ① 入队 Consolidator：token 累计或轮数兜底
        val shouldConsolidate = accumulatedTokens >= consolidateConfig.tokenThreshold ||
            turnCounter % consolidateConfig.turnFallback == 0
        if (shouldConsolidate) {
            val snapshot = contextMemorySystem.snapshotRecentMessages(maxCount = 6)
            if (snapshot.isNotEmpty()) {
                val turnRange = "T${turnCounter - snapshot.size / 2 + 1}-T$turnCounter"
                worker.enqueue(IngestionJob.ConsolidateJob(snapshot, turnRange))
            }
            accumulatedTokens = 0
        }

        // ② 入队 FactExtractor：每轮 enqueue，由 worker 内部的 ShouldIngest 决定跑不跑
        val recent = contextMemorySystem.snapshotRecentMessages(maxCount = 4)
        if (recent.isNotEmpty()) {
            worker.enqueue(IngestionJob.ExtractFactsJob(recent))
        }

        // ③ 重置空闲计时器：最后一次互动后 idleMs 触发 Dream
        scheduleIdleDream()
    }

    /**
     * 重启空闲计时器。每轮 afterReply 都重置，5 分钟无互动后触发 Dream。
     */
    private fun scheduleIdleDream() {
        val dream = dreamJob ?: return
        idleDreamJob?.cancel()
        idleDreamJob = backgroundScope.launch {
            try {
                delay(dreamConfig.idleMs)
                Log.d(TAG, "idle reached, running Dream")
                dream.run(force = false)
            } catch (e: Throwable) {
                // 被 cancel 或 Dream 抛错都吞掉，等下一次轮询触发
            }
        }
    }

    /**
     * Activity onPause 时调用。仅 flush 写入流水线，不跑 Dream。
     * 见 MEMORY_SYSTEM_DESIGN.md 第六章。
     */
    suspend fun flushOnPause() {
        idleDreamJob?.cancel()
        idleDreamJob = null
        ingestionWorker?.flush()
    }

    /**
     * 调试用：手动强制触发 Dream（绕过 idle 计时器）
     */
    fun forceDreamNow() {
        val dream = dreamJob ?: return
        backgroundScope.launch { dream.run(force = true) }
    }

    /**
     * 极简 token 估算：中文按字符 0.7、英文按 0.25 折算。
     * 仅用于 Consolidator 触发判断，精度无所谓。
     */
    private fun estimateTokens(text: String): Int {
        if (text.isEmpty()) return 0
        var chinese = 0
        var other = 0
        for (c in text) {
            if (c.code in 0x4E00..0x9FFF) chinese++ else other++
        }
        return (chinese * 0.7 + other * 0.25).toInt()
    }
}
