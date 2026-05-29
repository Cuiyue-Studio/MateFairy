package com.example.matefairy01.memory.db

import android.content.Context
import androidx.room.Room

/**
 * Application-scope 的 Room 数据库单例工厂。
 *
 * 由 [com.example.matefairy01.runtime.MateFairyRuntimeFactory] 在装配阶段调用一次，
 * 之后通过 [MateFairyRuntime.database] 取用。
 */
object DbProvider {

    private const val DB_NAME = "matefairy_memory.db"

    @Volatile
    private var instance: MateFairyDatabase? = null

    fun get(context: Context): MateFairyDatabase {
        return instance ?: synchronized(this) {
            instance ?: build(context.applicationContext).also { instance = it }
        }
    }

    private fun build(appContext: Context): MateFairyDatabase {
        return Room.databaseBuilder(
            appContext,
            MateFairyDatabase::class.java,
            DB_NAME
        )
            // P1 阶段不做 migration，schema 改动直接清库；P2 再切自动迁移
            .fallbackToDestructiveMigration()
            .build()
    }
}
