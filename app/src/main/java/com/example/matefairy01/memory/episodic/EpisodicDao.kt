package com.example.matefairy01.memory.episodic

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface EpisodicDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entry: EpisodicEntry): Long

    @Update
    suspend fun update(entry: EpisodicEntry): Int

    @Query("DELETE FROM episodic WHERE rowid = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM episodic WHERE rowid = :id")
    suspend fun findById(id: Long): EpisodicEntry?

    @Query("SELECT COUNT(*) FROM episodic")
    suspend fun count(): Int

    /**
     * 取近 [sinceTimestamp] 起的所有条目，按时间倒序，最多 [limit] 条。
     * 用作向量召回前的时间窗预过滤（避免对全表算 cosine）。
     */
    @Query("SELECT * FROM episodic WHERE timestamp >= :sinceTimestamp ORDER BY timestamp DESC LIMIT :limit")
    suspend fun recentSince(sinceTimestamp: Long, limit: Int): List<EpisodicEntry>

    /**
     * FTS MATCH 候选搜索。query 需符合 FTS4 MATCH 语法（一般传 token 即可）。
     * 用作向量召回的辅助候选生成；中文召回质量有限，故 limit 给得宽。
     */
    @Query(
        """
        SELECT episodic.* FROM episodic
        JOIN episodic_fts ON episodic_fts.rowid = episodic.rowid
        WHERE episodic_fts MATCH :query
        ORDER BY episodic.timestamp DESC
        LIMIT :limit
        """
    )
    suspend fun ftsSearch(query: String, limit: Int): List<EpisodicEntry>

    @Query("DELETE FROM episodic")
    suspend fun deleteAll(): Int
}
