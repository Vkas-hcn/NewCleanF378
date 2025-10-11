package com.desolation.spreads.reach.yy.model

import android.graphics.drawable.Drawable

data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val installTime: Long,
    val isSystemApp: Boolean
)