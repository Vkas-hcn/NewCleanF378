package com.desolation.spreads.reach

data class FileItem(
    val name: String,
    val path: String,
    val size: Long,
    val lastModified: Long,
    var isSelected: Boolean = false
)
