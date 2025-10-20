package com.demo

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.webkit.WebView

/**
 * Date：2025/10/20
 * Describe:
 * todo 如果是pangle单聚合可以不要这个进程判断
 * 这些都是demo 类
 */
class DemoProgressCheck {


    // 是否是主进程
    fun isMainProgress(context: Context): Boolean {
        if (context.packageName == context.getProName()) {
            return true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName: String = Application.getProcessName()
            if (processName.isNotBlank()) {
                WebView.setDataDirectorySuffix(processName)
            }
        }
        return false
    }

    private fun Context.getProName(): String {
        runCatching {
            val am = getSystemService(Application.ACTIVITY_SERVICE) as ActivityManager
            val runningApps = am.runningAppProcesses ?: return ""
            for (info in runningApps) {
                when (info.pid) {
                    android.os.Process.myPid() -> return info.processName
                }
            }
        }
        return ""
    }
}