package com.des.show.bee.qlwj.util


object FileSizeFormatter {


    fun formatFileSizeToParts(size: Long): Pair<String, String> {
        return when {
            size >= 1024 * 1024 * 1024 -> {
                Pair(String.format("%.1f", size / (1024.0 * 1024.0 * 1024.0)), "GB")
            }
            size >= 1024 * 1024 -> {
                Pair(String.format("%.1f", size / (1024.0 * 1024.0)), "MB")
            }
            else -> {
                Pair(String.format("%.1f", size / 1024.0), "KB")
            }
        }
    }


    fun formatFileSize(size: Long, precision: Int = 2): String {
        val formatPattern = "%.${precision}f"
        return when {
            size >= 1024 * 1024 * 1024 -> String.format(formatPattern, size / (1024.0 * 1024.0 * 1024.0)) + "GB"
            size >= 1024 * 1024 -> String.format(formatPattern, size / (1024.0 * 1024.0)) + "MB"
            else -> String.format(formatPattern, size / 1024.0) + "KB"
        }
    }
}