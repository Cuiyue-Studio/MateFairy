package com.example.matefairy01.ml

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 端侧向量计算与序列化工具。
 *
 * 设计目标：
 * - cosine：N < 2000 的暴力召回够快（ARM64 单条 < 0.05ms）。
 * - toBytes / fromBytes：固定 little-endian 4-byte float，方便存进 Room 的 BLOB 列。
 *
 * 仅支持 [FloatArray]；如果未来切到量化向量再扩展。
 */
object VectorMath {

    /** 余弦相似度。两端任一为零向量返回 0。已假定输入为 L2 normalized。 */
    fun cosine(a: FloatArray, b: FloatArray): Float {
        if (a.size != b.size) return 0f
        var dot = 0f
        for (i in a.indices) dot += a[i] * b[i]
        return dot
    }

    fun cosineUnsafe(a: FloatArray, b: FloatArray): Float {
        var dot = 0f
        var na = 0f
        var nb = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            na += a[i] * a[i]
            nb += b[i] * b[i]
        }
        val denom = kotlin.math.sqrt(na * nb)
        return if (denom < 1e-12f) 0f else dot / denom
    }

    fun toBytes(vec: FloatArray): ByteArray {
        val buf = ByteBuffer.allocate(vec.size * 4).order(ByteOrder.LITTLE_ENDIAN)
        for (v in vec) buf.putFloat(v)
        return buf.array()
    }

    fun fromBytes(bytes: ByteArray, dimension: Int): FloatArray {
        require(bytes.size == dimension * 4) {
            "byte size ${bytes.size} mismatch with dim $dimension (expected ${dimension * 4})"
        }
        val out = FloatArray(dimension)
        val buf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        for (i in 0 until dimension) out[i] = buf.float
        return out
    }
}
