package com.example.matefairy01.memory.permanent

import android.content.Context
import android.util.Log
import java.io.File

/**
 * L4 永久层的文件读写 + 备份 + 首次模板拷贝。
 *
 * 文件路径： `/data/data/<pkg>/files/memory/{SOUL,USER,MEMORY}.md`
 *
 * 关键约束：
 * - 写入采用 `tmp + rename` 原子操作，防止中途被打断留半成品
 * - 写入前 copy 当前 → `*.bak`，作为最简单的版本备份（P2 升级 JGit）
 * - 首次启动 [ensureInitialized] 自动从 [MdTemplates] 拷贝，**不会覆盖已有文件**
 */
class PermanentStore(
    private val context: Context
) {
    companion object {
        private const val TAG = "PermanentStore"
        private const val DIR_NAME = "memory"
    }

    private val memoryDir: File by lazy {
        File(context.filesDir, DIR_NAME).also { it.mkdirs() }
    }

    /** 在首次构造或 application onCreate 时调用一次。幂等。 */
    fun ensureInitialized() {
        ensureTemplate(MdTemplates.FILE_SOUL, MdTemplates.SOUL)
        ensureTemplate(MdTemplates.FILE_USER, MdTemplates.USER)
        ensureTemplate(MdTemplates.FILE_MEMORY, MdTemplates.MEMORY)
    }

    fun readSoul(): String = readSafe(MdTemplates.FILE_SOUL)
    fun readUser(): String = readSafe(MdTemplates.FILE_USER)
    fun readMemory(): String = readSafe(MdTemplates.FILE_MEMORY)

    /** 写入用户画像，自动备份 + 原子写 */
    fun writeUser(content: String) = writeSafe(MdTemplates.FILE_USER, content)

    /** 写入叙事记忆，自动备份 + 原子写 */
    fun writeMemory(content: String) = writeSafe(MdTemplates.FILE_MEMORY, content)

    /** SOUL 由开发者 / 用户编辑，不开放程序写入 API（Dream 流程不应改它） */

    fun memoryDirPath(): String = memoryDir.absolutePath

    // ------------------------------------------------------------------

    private fun ensureTemplate(name: String, template: String) {
        val f = File(memoryDir, name)
        if (f.exists() && f.length() > 0L) return
        runCatching {
            f.writeText(template.trim() + "\n")
            Log.d(TAG, "template written: $name")
        }.onFailure { Log.w(TAG, "template write failed for $name: ${it.message}") }
    }

    private fun readSafe(name: String): String {
        val f = File(memoryDir, name)
        return runCatching { if (f.exists()) f.readText() else "" }.getOrDefault("")
    }

    private fun writeSafe(name: String, content: String) {
        val target = File(memoryDir, name)
        backupIfExists(target)

        val tmp = File(memoryDir, "$name.tmp")
        runCatching {
            tmp.writeText(content)
            // rename = 原子操作（同一文件系统）
            if (target.exists()) target.delete()
            if (!tmp.renameTo(target)) {
                target.writeText(content)
                tmp.delete()
            }
            Log.d(TAG, "wrote $name (${content.length} chars)")
        }.onFailure {
            Log.w(TAG, "write failed for $name: ${it.message}")
            tmp.delete()
        }
    }

    private fun backupIfExists(src: File) {
        if (!src.exists()) return
        val bak = File(memoryDir, src.name + MdTemplates.BACKUP_SUFFIX)
        runCatching { src.copyTo(bak, overwrite = true) }
            .onFailure { Log.w(TAG, "backup failed for ${src.name}: ${it.message}") }
    }
}
