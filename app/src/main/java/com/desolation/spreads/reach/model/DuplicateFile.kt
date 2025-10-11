package com.desolation.spreads.reach.model

import java.io.File

data class DuplicateFile(
    val name: String,
    val path: String,
    val size: Long,
    val md5: String,
    val isImage: Boolean = false,
    val dateModified: Long = 0L,
    var isSelected: Boolean = false
)

data class DuplicateCategory(
    val md5: String,
    val files: MutableList<DuplicateFile>,
    val isImageCategory: Boolean = false,
    val dateGroup: String? = null // For image grouping by date
)