package com.example.matefairy01.memory.semantic

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TripleDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(triple: KnowledgeTriple): Long

    @Query("SELECT * FROM triples WHERE rowid = :id")
    suspend fun findById(id: Long): KnowledgeTriple?

    @Query("SELECT * FROM triples WHERE subject = :subject ORDER BY createdAt DESC LIMIT :limit")
    suspend fun findBySubject(subject: String, limit: Int): List<KnowledgeTriple>

    @Query("SELECT * FROM triples WHERE obj = :obj ORDER BY createdAt DESC LIMIT :limit")
    suspend fun findByObject(obj: String, limit: Int): List<KnowledgeTriple>

    @Query("SELECT COUNT(*) FROM triples")
    suspend fun count(): Int

    @Query("DELETE FROM triples")
    suspend fun deleteAll(): Int
}
