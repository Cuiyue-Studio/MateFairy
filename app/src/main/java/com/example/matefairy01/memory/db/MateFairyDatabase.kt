package com.example.matefairy01.memory.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.matefairy01.memory.episodic.EpisodicDao
import com.example.matefairy01.memory.episodic.EpisodicEntry
import com.example.matefairy01.memory.episodic.EpisodicFts
import com.example.matefairy01.memory.semantic.Fact
import com.example.matefairy01.memory.semantic.FactDao
import com.example.matefairy01.memory.semantic.KnowledgeTriple
import com.example.matefairy01.memory.semantic.TripleDao

/**
 * MateFairy 记忆系统持久层。集中管理 L2 episodic / L3 semantic 两类表。
 *
 * 版本策略：
 * - P1 阶段不实现 migration，schema 改动直接破坏性升级（[Builder.fallbackToDestructiveMigration]）。
 * - P2 引入版本号 + autoMigrations 之后再升级到 version=2。
 */
@Database(
    entities = [
        EpisodicEntry::class,
        EpisodicFts::class,
        Fact::class,
        KnowledgeTriple::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MateFairyDatabase : RoomDatabase() {
    abstract fun episodicDao(): EpisodicDao
    abstract fun factDao(): FactDao
    abstract fun tripleDao(): TripleDao
}
