package com.example.matefairy01.memory.semantic

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface FactDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(fact: Fact): Long

    @Update
    suspend fun update(fact: Fact): Int

    @Query("SELECT * FROM facts WHERE rowid = :id")
    suspend fun findById(id: Long): Fact?

    @Query("SELECT COUNT(*) FROM facts")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM facts WHERE supersededBy IS NULL")
    suspend fun countActive(): Int

    /** 仅活跃事实（未被 supersede） */
    @Query("SELECT * FROM facts WHERE supersededBy IS NULL ORDER BY updatedAt DESC LIMIT :limit")
    suspend fun listActive(limit: Int): List<Fact>

    @Query(
        """
        SELECT * FROM facts
        WHERE supersededBy IS NULL AND category = :category
        ORDER BY updatedAt DESC
        LIMIT :limit
        """
    )
    suspend fun listActiveByCategory(category: String, limit: Int): List<Fact>

    /**
     * 按 category 取活跃事实做候选（向量召回前的预过滤）；
     * category 为 null 时不过滤。
     */
    @Query(
        """
        SELECT * FROM facts
        WHERE supersededBy IS NULL AND (:category IS NULL OR category = :category)
        ORDER BY updatedAt DESC
        LIMIT :limit
        """
    )
    suspend fun listCandidates(category: String?, limit: Int): List<Fact>

    /** 把旧事实标记为被某新事实替代，更新 updatedAt */
    @Query(
        """
        UPDATE facts SET supersededBy = :newId, updatedAt = :now
        WHERE rowid = :oldId
        """
    )
    suspend fun supersede(oldId: Long, newId: Long, now: Long): Int

    @Query("DELETE FROM facts")
    suspend fun deleteAll(): Int
}
