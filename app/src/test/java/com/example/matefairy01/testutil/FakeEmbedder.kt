package com.example.matefairy01.testutil

import com.example.matefairy01.ml.IEmbedder
import kotlin.math.sqrt

/**
 * 确定性假 embedder，仅供离线单测使用。
 *
 * 把文本的字符 unigram + bigram 哈希散列到固定维度的桶里再做 L2 normalize。
 * 性质：
 *  - 同一文本 → 完全相同的向量 → VectorMath.cosine（点积）= 1.0
 *  - 共享越多子串的文本 → cosine 越高
 *  - 不依赖任何网络 / API key
 */
class FakeEmbedder(override val dimension: Int = 32) : IEmbedder {

    override val isReady: Boolean = true

    override suspend fun warmup(): Boolean = true

    override suspend fun encode(text: String): FloatArray {
        val v = FloatArray(dimension)
        val s = text.trim()
        if (s.isEmpty()) return v

        for (i in s.indices) {
            val uni = s[i].code
            v[Math.floorMod(uni, dimension)] += 1f
            if (i + 1 < s.length) {
                val bi = s[i].code * 31 + s[i + 1].code
                v[Math.floorMod(bi, dimension)] += 1f
            }
        }

        var norm = 0f
        for (x in v) norm += x * x
        norm = sqrt(norm)
        if (norm > 0f) {
            for (i in v.indices) v[i] /= norm
        }
        return v
    }
}
