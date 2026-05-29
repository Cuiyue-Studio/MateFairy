package com.example.matefairy01.memory.retrieval

import kotlin.math.pow

/**
 * 时间衰减打分。半衰期 [halfLifeDays] 天后得分衰减到 0.5。
 *
 * 公式：score = 0.5 ^ (Δdays / halfLifeDays)
 *   - Δt = 0 → 1.0
 *   - Δt = halfLifeDays → 0.5
 *   - Δt = 2 * halfLifeDays → 0.25
 *   - 未来时间戳（Δt < 0） → 1.0（钳制）
 *
 * 来源：Generative Agents (Park et al., Stanford, 2023) 三因子检索的 recency 项。
 */
class RecencyScorer(private val halfLifeDays: Float) {

    fun score(now: Long, timestamp: Long): Float {
        val deltaMs = (now - timestamp).toDouble().coerceAtLeast(0.0)
        val deltaDays = deltaMs / MS_PER_DAY
        return 0.5.pow(deltaDays / halfLifeDays).toFloat()
    }

    companion object {
        private const val MS_PER_DAY: Double = 24.0 * 3600.0 * 1000.0
    }
}
