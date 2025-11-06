package com.des.show.bee.qlwj.data.model


enum class TrashType {
    APP_CACHE,
    APK_FILES,
    LOG_FILES,
    TEMP_FILES,
    OTHER
}


data class TrashFile(
    val name: String,
    val path: String,
    val size: Long,
    var isSelected: Boolean = false,
    val type: TrashType
)


data class TrashCategory(
    val name: String,
    val files: MutableList<TrashFile>,
    val type: TrashType,
    var isExpanded: Boolean = false,
    var isSelected: Boolean = false,
    var totalSize: Long = 0L
)