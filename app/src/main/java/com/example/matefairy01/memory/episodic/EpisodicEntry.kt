package com.example.matefairy01.memory.episodic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * L2 情节记忆条目。每条对应一段被 Consolidator 摘要过的会话片段。
 *
 * 注意：
 * - PK 命名为 `rowid` 是为了让 [EpisodicFts] 的 `contentEntity` 自动同步生效。
 * - [embedding] 用 ByteArray 存 little-endian 4-byte float（见 [com.example.matefairy01.ml.VectorMath]）。
 *   stub 模式（远端 API 缺 key）写入时为零向量序列化或 null，由 Store 决定。
 * - data class + ByteArray 必须手动覆盖 equals/hashCode，否则单测和集合行为会假阳性。
 */
@Entity(
    tableName = "episodic",
    indices = [Index("timestamp"), Index("importance")]
)
data class EpisodicEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Long = 0L,

    /** 摘要文本，FTS 索引同步该列 */
    val content: String,

    /** 写入时刻（System.currentTimeMillis） */
    val timestamp: Long,

    /** LLM 自评的重要性 1-10，参与三因子打分 */
    val importance: Int,

    /** L2 normalized embedding 序列化字节；远端不可用或 stub 时为 null */
    val embedding: ByteArray?,

    /** 源会话轮次区间，例如 "12-21"，用于 prompt 引用与回放调试 */
    val sourceTurnRange: String? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EpisodicEntry) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

/**
 * FTS4 倒排索引镜像，contentEntity 指向 [EpisodicEntry]，Room 会通过触发器自动同步 content 列。
 *
 * 已知限制：unicode61 tokenizer 对中文按字符切分，召回质量一般；
 * 在 P1 设计中作为候选生成 + 排序加权使用，主排序仍然依赖向量 cosine + recency。
 */
@Fts4(contentEntity = EpisodicEntry::class)
@Entity(tableName = "episodic_fts")
data class EpisodicFts(
    val content: String
)
