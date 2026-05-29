package com.example.matefairy01.memory.semantic

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * L3 扁平事实记忆。覆盖 90% 的陪伴场景：用户偏好、技能、习惯、身份信息等。
 *
 * 关键机制：
 * - 不删除，用 [supersededBy] 标记被替代；保留事实演化轨迹便于审计
 * - [embedding] 用于语义召回（"我之前说过对什么过敏？" 命中"对花生过敏"）
 *
 * 类别枚举（自由扩展，常用值）：preference / dislike / skill / habit / identity / relation / event
 */
@Entity(
    tableName = "facts",
    indices = [
        Index("category"),
        Index("supersededBy"),
        Index("createdAt")
    ]
)
data class Fact(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "rowid")
    val id: Long = 0L,

    /** 类别 tag，便于按维度筛选 */
    val category: String,

    /** 事实文本，例如 "用户喜欢喝美式咖啡" */
    val content: String,

    /** LLM 抽取置信度 0..1 */
    val confidence: Float,

    /** L2 normalized embedding 序列化字节 */
    val embedding: ByteArray,

    /** 被哪条新事实替代；null 表示当前活跃 */
    val supersededBy: Long? = null,

    val createdAt: Long,
    val updatedAt: Long
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Fact) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
