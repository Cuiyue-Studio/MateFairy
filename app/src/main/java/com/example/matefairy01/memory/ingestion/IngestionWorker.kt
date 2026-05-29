package com.example.matefairy01.memory.ingestion

import android.util.Log
import com.example.matefairy01.memory.IngestionConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * 异步任务队列：把 [IngestionJob] 投递给单消费者协程串行处理，
 * 保证主对话路径**永远不等**。
 *
 * 关键约束：
 * - 单消费者协程 + Channel(UNLIMITED) → 简单，FIFO 顺序
 * - 失败任务仅打 warning，不重试不阻塞后续任务（避免雪崩）
 * - [flush] 在 Activity onPause 时调用，按 [IngestionConfig.flushTimeoutPerJobMs] 限时跑完积压任务
 *
 * 不要用 lifecycleScope 持有 worker；它必须 application-scope 长寿命。
 */
class IngestionWorker(
    private val consolidator: Consolidator,
    private val factExtractor: FactExtractor,
    private val shouldIngest: ShouldIngest,
    private val ingestionConfig: IngestionConfig,
    parentScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
) {
    companion object {
        private const val TAG = "IngestionWorker"
    }

    private val scope: CoroutineScope = CoroutineScope(parentScope.coroutineContext + SupervisorJob())
    private val channel: Channel<IngestionJob> = Channel(Channel.UNLIMITED)

    @Volatile
    private var consumerJob: Job? = null

    init {
        start()
    }

    /** 投递任务，永不阻塞 */
    fun enqueue(job: IngestionJob) {
        val result = channel.trySend(job)
        if (!result.isSuccess) {
            Log.w(TAG, "enqueue failed (channel closed?): job=${job::class.simpleName}")
        }
    }

    /**
     * 跑完当前积压（不接受新任务的快速通道）。
     * 每个任务限时 [IngestionConfig.flushTimeoutPerJobMs]，超时丢回队列下次再做。
     */
    suspend fun flush() {
        val timeoutMs = ingestionConfig.flushTimeoutPerJobMs
        var processed = 0
        // 注意：不关闭 channel；只是把当前可用的任务尽快拉空
        while (true) {
            val job = channel.tryReceive().getOrNull() ?: break
            try {
                withTimeout(timeoutMs) { handle(job) }
                processed++
            } catch (e: TimeoutCancellationException) {
                Log.w(TAG, "flush job timeout (${job::class.simpleName}), re-enqueue")
                enqueue(job)
                break
            } catch (e: Throwable) {
                Log.w(TAG, "flush job failed: ${e.message}")
            }
        }
        if (processed > 0) Log.d(TAG, "flush processed $processed jobs")
    }

    /** 应用退出时调用一次（一般不需要主动调，application 进程结束自然回收） */
    fun shutdown() {
        runCatching { channel.close() }
        runCatching { scope.cancel() }
    }

    // ------------------------------------------------------------------

    private fun start() {
        if (consumerJob != null) return
        consumerJob = scope.launch {
            try {
                channel.consumeEach { job ->
                    if (!isActive) return@consumeEach
                    try {
                        handle(job)
                    } catch (e: Throwable) {
                        Log.w(TAG, "job ${job::class.simpleName} failed: ${e.message}", e)
                    }
                    // 微小让步，避免连续高强度 LLM 调用打 API 限流
                    delay(50)
                }
            } catch (e: Throwable) {
                Log.w(TAG, "consumer loop terminated: ${e.message}", e)
            }
        }
    }

    private suspend fun handle(job: IngestionJob) {
        when (job) {
            is IngestionJob.ConsolidateJob -> {
                consolidator.run(job.messages, job.turnRange)
            }
            is IngestionJob.ExtractFactsJob -> {
                if (!shouldIngest.check(job.messages)) {
                    Log.d(TAG, "shouldIngest rejected; skip extract")
                    return
                }
                val ids = factExtractor.extract(job.messages)
                if (ids.isNotEmpty()) shouldIngest.markIngested()
            }
        }
    }
}
