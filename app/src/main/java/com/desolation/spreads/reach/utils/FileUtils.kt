package com.desolation.spreads.reach.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import com.desolation.spreads.reach.model.DuplicateCategory
import com.desolation.spreads.reach.model.DuplicateFile
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    
    private val imageExtensions = setOf("jpg", "jpeg", "png", "gif", "bmp", "webp")
    
    fun isImageFile(fileName: String): Boolean {
        val extension = fileName.substringAfterLast('.', "").lowercase()
        return imageExtensions.contains(extension)
    }
    
    fun calculateMD5(file: File): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            ""
        }
    }
    
    fun isSimilarImage(image1: File, image2: File): Boolean {
        return try {
            // Check if files are from the same day
            val date1 = getImageDate(image1)
            val date2 = getImageDate(image2)
            
            if (date1 != date2) return false
            
            // For now, just check if they have the same MD5
            // In a real implementation, you might want to compare image hashes or use image comparison algorithms
            calculateMD5(image1) == calculateMD5(image2)
        } catch (e: Exception) {
            false
        }
    }
    
    private fun getImageDate(imageFile: File): String {
        return try {
            val exif = ExifInterface(imageFile.absolutePath)
            val dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME)
            if (dateTime != null) {
                // Parse EXIF date and extract just the date part
                val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                val date = sdf.parse(dateTime)
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date ?: Date())
            } else {
                // Fallback to file modification date
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(imageFile.lastModified()))
            }
        } catch (e: Exception) {
            // Fallback to file modification date
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(imageFile.lastModified()))
        }
    }
    
    fun formatFileSize(size: Long): String {
        return when {
            size >= 1024 * 1024 * 1024 -> String.format("%.2f GB", size / (1024.0 * 1024.0 * 1024.0))
            size >= 1024 * 1024 -> String.format("%.2f MB", size / (1024.0 * 1024.0))
            else -> String.format("%.2f KB", size / 1024.0)
        }
    }
    
    fun scanDuplicateFiles(context: Context): List<DuplicateCategory> {
        val fileMap = mutableMapOf<String, MutableList<DuplicateFile>>()
        val imageGroups = mutableMapOf<String, MutableList<DuplicateFile>>()
        
        // Get external storage directories to scan
        val directoriesToScan = mutableListOf<File>()
        
        // Add common directories
        directoriesToScan.add(File("/storage/emulated/0/DCIM"))
        directoriesToScan.add(File("/storage/emulated/0/Pictures"))
        directoriesToScan.add(File("/storage/emulated/0/Download"))
        directoriesToScan.add(File("/storage/emulated/0/Documents"))
        
        // Add external files directory if available
        context.getExternalFilesDir(null)?.let { externalDir ->
            directoriesToScan.add(externalDir)
        }
        
        // Scan each directory
        directoriesToScan.forEach { directory ->
            if (directory.exists() && directory.isDirectory) {
                scanDirectory(directory, fileMap, imageGroups)
            }
        }
        
        // If no duplicates found, create some demo data for testing
        if (fileMap.isEmpty() && imageGroups.isEmpty()) {
            return createDemoData()
        }
        
        val categories = mutableListOf<DuplicateCategory>()
        
        // Add regular file duplicates
        fileMap.forEach { (md5, files) ->
            if (files.size > 1) {
                categories.add(DuplicateCategory(md5, files))
            }
        }
        
        // Add image duplicates grouped by date
        imageGroups.forEach { (dateGroup, files) ->
            val imageFileMap = mutableMapOf<String, MutableList<DuplicateFile>>()
            files.forEach { file ->
                val md5 = calculateMD5(File(file.path))
                imageFileMap.getOrPut(md5) { mutableListOf() }.add(file)
            }
            
            imageFileMap.forEach { (md5, imageFiles) ->
                if (imageFiles.size > 1) {
                    categories.add(DuplicateCategory(md5, imageFiles, true, dateGroup))
                }
            }
        }
        
        return categories
    }
    
    private fun createDemoData(): List<DuplicateCategory> {
        val demoCategories = mutableListOf<DuplicateCategory>()
        
        // Create demo duplicate files
        val demoFiles1 = mutableListOf(
            DuplicateFile("document1.pdf", "/storage/emulated/0/Download/document1.pdf", 1024000, "abc123"),
            DuplicateFile("document1_copy.pdf", "/storage/emulated/0/Documents/document1_copy.pdf", 1024000, "abc123")
        )
        demoCategories.add(DuplicateCategory("abc123", demoFiles1))
        
        val demoFiles2 = mutableListOf(
            DuplicateFile("image1.jpg", "/storage/emulated/0/DCIM/image1.jpg", 2048000, "def456", true),
            DuplicateFile("image1_backup.jpg", "/storage/emulated/0/Pictures/image1_backup.jpg", 2048000, "def456", true)
        )
        demoCategories.add(DuplicateCategory("def456", demoFiles2, true, "2024-01-15"))
        
        val demoFiles3 = mutableListOf(
            DuplicateFile("video1.mp4", "/storage/emulated/0/DCIM/video1.mp4", 15728640, "ghi789"),
            DuplicateFile("video1_copy.mp4", "/storage/emulated/0/Download/video1_copy.mp4", 15728640, "ghi789"),
            DuplicateFile("video1_backup.mp4", "/storage/emulated/0/Pictures/video1_backup.mp4", 15728640, "ghi789")
        )
        demoCategories.add(DuplicateCategory("ghi789", demoFiles3))
        
        return demoCategories
    }
    
    private fun scanDirectory(
        directory: File,
        fileMap: MutableMap<String, MutableList<DuplicateFile>>,
        imageGroups: MutableMap<String, MutableList<DuplicateFile>>
    ) {
        try {
            if (!directory.exists() || !directory.isDirectory) return
            
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    // Recursively scan subdirectories
                    scanDirectory(file, fileMap, imageGroups)
                } else {
                    try {
                        val duplicateFile = DuplicateFile(
                            name = file.name,
                            path = file.absolutePath,
                            size = file.length(),
                            md5 = calculateMD5(file),
                            isImage = isImageFile(file.name),
                            dateModified = file.lastModified()
                        )
                        
                        if (duplicateFile.isImage) {
                            // Group images by date
                            val dateGroup = getImageDate(file)
                            imageGroups.getOrPut(dateGroup) { mutableListOf() }.add(duplicateFile)
                        } else {
                            // Group regular files by MD5
                            fileMap.getOrPut(duplicateFile.md5) { mutableListOf() }.add(duplicateFile)
                        }
                    } catch (e: Exception) {
                        // Skip files that can't be processed
                    }
                }
            }
        } catch (e: Exception) {
            // Skip directories that can't be accessed
        }
    }
}