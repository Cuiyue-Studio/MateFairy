package com.example.matefairy01.memory.semantic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * L3 知识图谱三元组，schema 留位用于 P2 多跳关系查询。
 *
 * P1 阶段不实施 LLM 抽取，仅提供基础 CRUD，便于将来无破坏性升级。
 *
 * 类名加 `Knowledge` 前缀以避免与 [kotlin.Triple] 标准库冲突。
 */
@Entity(
    tableName = "triples",
    indices = [
        Index("subject"),
        Index("obj"),
        Index("predicate")
    ]
)
data class KnowledgeTriple(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Long = 0L,

    /** 主体，例如 "用户" / "朋友A" */
    val subject: String,

    /** 关系，例如 "喜欢" / "生日是" */
    val predicate: String,

    /** 客体，例如 "咖啡" / "1990-01-01" */
    @ColumnInfo(name = "obj")
    val obj: String,

    val confidence: Float,

    /** 来源 episodic 条目 id，便于回溯证据 */
    val sourceEpisodeId: Long? = null,

    val createdAt: Long
)
