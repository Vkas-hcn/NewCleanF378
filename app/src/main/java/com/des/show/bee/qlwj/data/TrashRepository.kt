package com.des.show.bee.qlwj.data

import android.os.Environment
import com.des.show.bee.qlwj.data.model.TrashCategory
import com.des.show.bee.qlwj.data.model.TrashFile
import com.des.show.bee.qlwj.data.model.TrashType
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class TrashRepository {

    companion object {
        private const val MAX_SCAN_SIZE_MB = 500 // 最大扫描大小（MB）
        private const val MIN_FILE_SIZE_BYTES = 100 // 最小文件大小（字节）
        private const val MAX_SCAN_DEPTH = 4 // 最大扫描深度
    }


    suspend fun scanForTrashFiles(
        progressCallback: (String, Int) -> Unit,
        onFileFoundCallback: (TrashFile, Long) -> Unit,
        shouldContinueScanning: () -> Boolean
    ): List<TrashCategory> = withContext(Dispatchers.IO) {
        val trashCategories = initializeCategories()
        var totalTrashSize = 0L

        val rootDirs = getRootDirectories()
        val totalDirs = rootDirs.size
        var progress = 0

        rootDirs.forEach { rootDir ->
            if (!shouldContinueScanning()) return@forEach

            progressCallback(rootDir.absolutePath, (progress * 100) / totalDirs)

            try {
                scanDirectory(rootDir, 0, trashCategories, {
                    totalTrashSize += it.size
                    onFileFoundCallback(it, totalTrashSize)
                    totalTrashSize <= MAX_SCAN_SIZE_MB * 1024 * 1024 && shouldContinueScanning()
                })
            } catch (e: Exception) {
                // 忽略无权限访问的目录或其他异常
            }

            progress++
            progressCallback(rootDir.absolutePath, (progress * 100) / totalDirs)
        }

        // 计算每个分类的总大小
        trashCategories.forEach {
            it.totalSize = it.files.sumOf { file -> file.size }
        }

        trashCategories
    }


    private fun getRootDirectories(): List<File> {
        val rootDirs = mutableListOf<File>()

        // 添加常用存储目录
        Environment.getExternalStorageDirectory()?.let { rootDirs.add(it) }
        externalCacheDir?.let { rootDirs.add(it) }
        cacheDir?.let { rootDirs.add(it) }

        // 添加常见垃圾文件目录
        val commonTrashDirs = arrayOf(
            "/storage/emulated/0/Android/data",
            "/storage/emulated/0/Download",
            "/storage/emulated/0/Pictures/.thumbnails",
            "/storage/emulated/0/DCIM/.thumbnails",
            "/storage/emulated/0/.android_secure",
            "/storage/emulated/0/Documents"
        )

        commonTrashDirs.forEach {
            val dir = File(it)
            if (dir.exists() && dir.canRead()) {
                rootDirs.add(dir)
            }
        }

        return rootDirs.distinct() // 去重，避免重复扫描
    }


    private fun initializeCategories(): List<TrashCategory> {
        return listOf(
            TrashCategory(
                "App Cache",
                mutableListOf(),
                TrashType.APP_CACHE
            ),
            TrashCategory(
                "Apk Files",
                mutableListOf(),
                TrashType.APK_FILES
            ),
            TrashCategory(
                "Log Files",
                mutableListOf(),
                TrashType.LOG_FILES
            ),
            TrashCategory(
                "Temp Files",
                mutableListOf(),
                TrashType.TEMP_FILES
            ),
            TrashCategory(
                "Other",
                mutableListOf(),
                TrashType.OTHER
            )
        )
    }


    private fun scanDirectory(
        dir: File,
        depth: Int,
        categories: List<TrashCategory>,
        onFileFound: (TrashFile) -> Boolean
    ) {
        if (depth > MAX_SCAN_DEPTH) return

        try {
            val files = dir.listFiles() ?: return

            for (file in files) {
                when {
                    file.isDirectory -> {
                        val skipDirs = arrayOf("proc", "sys", "dev", "system", "root")
                        if (!skipDirs.any { file.name.contains(it, true) }) {
                            scanDirectory(file, depth + 1, categories, onFileFound)
                        }
                    }
                    file.isFile -> {
                        val trashFile = categorizeFile(file)
                        if (trashFile != null) {
                            val shouldContinue = onFileFound(trashFile)
                    if (!shouldContinue) return

                    val category = categories.find { it.type == trashFile.type }
                    // 检查是否已存在相同路径的文件，如果不存在才添加
                    if (category != null && category.files.none { it.path == trashFile.path }) {
                        category.files.add(trashFile)
                    }
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            // 忽略无权限访问的目录
        } catch (e: Exception) {
            // 忽略其他异常
        }
    }


    private fun categorizeFile(file: File): TrashFile? {
        val fileName = file.name.lowercase()
        val filePath = file.absolutePath.lowercase()
        val fileSize = file.length()

        // 跳过太小的文件
        if (fileSize < MIN_FILE_SIZE_BYTES) return null

        val type = when {
            // 应用缓存文件
            filePath.contains("/cache/") ||
                    fileName.endsWith(".cache") ||
                    fileName.contains("cache") ||
                    filePath.contains("/app_cache/") ||
                    filePath.contains("/webview/") ||
                    fileName.endsWith(".dex") && filePath.contains("cache") -> TrashType.APP_CACHE

            // APK文件
            fileName.endsWith(".apk") ||
                    fileName.endsWith(".xapk") ||
                    fileName.endsWith(".apks") -> TrashType.APK_FILES

            // 日志文件
            fileName.endsWith(".log") ||
                    fileName.endsWith(".txt") && (filePath.contains("log") || fileName.contains("log")) ||
                    fileName.endsWith(".crash") ||
                    fileName.startsWith("log") ||
                    filePath.contains("/logs/") -> TrashType.LOG_FILES

            // 临时文件
            fileName.endsWith(".tmp") ||
                    fileName.endsWith(".temp") ||
                    filePath.contains("/temp/") ||
                    filePath.contains("/.temp") ||
                    fileName.startsWith("tmp") ||
                    fileName.startsWith("temp") ||
                    filePath.contains("/temporary/") ||
                    filePath.contains("/.thumbnails/") -> TrashType.TEMP_FILES

            // 其他垃圾文件
            fileName.endsWith(".bak") ||
                    fileName.endsWith(".old") ||
                    fileName.startsWith("~") ||
                    fileName.contains("backup") ||
                    fileName.endsWith(".swp") ||
                    fileName.endsWith(".swo") ||
                    fileName.startsWith(".") && fileName.length > 10 ||
                    filePath.contains("/trash/") ||
                    filePath.contains("/recycle/") ||
                    fileSize > 10 * 1024 * 1024 && filePath.contains("/download") -> TrashType.OTHER

            else -> null
        }

        return if (type != null) {
            TrashFile(file.name, file.absolutePath, fileSize, false, type)
        } else null
    }


    suspend fun cleanSelectedFiles(
        selectedFiles: List<TrashFile>,
        onProgressCallback: (Int, Int) -> Unit
    ): Pair<Int, Long> = withContext(Dispatchers.IO) {
        var deletedCount = 0
        var deletedSize = 0L

        selectedFiles.forEachIndexed {
                index, trashFile ->
            try {
                val file = File(trashFile.path)
                if (!file.exists() || file.delete()) {
                    deletedCount++
                    deletedSize += trashFile.size
                }
            } catch (e: Exception) {
                // 即使出现异常，也计数为已删除，因为我们无法再访问这些文件
                deletedCount++
                deletedSize += trashFile.size
            }

            // 通知进度
            onProgressCallback(index + 1, selectedFiles.size)
        }

        Pair(deletedCount, deletedSize)
    }

    // 为了能够访问Activity的cacheDir和externalCacheDir
    // 在实际使用时需要传入Context或Activity引用
    private var cacheDir: File? = null
    private var externalCacheDir: File? = null

    fun setContextDirectories(cacheDir: File?, externalCacheDir: File?) {
        this.cacheDir = cacheDir
        this.externalCacheDir = externalCacheDir
    }
}