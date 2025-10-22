package com.desolation.spreads.reach

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Process
import zj.go.NcZong

class GoShow: Application() {
    override fun onCreate() {
        super.onCreate()
        if (!isMainProcess(this)) {
            return
        }
        NcZong.initSp(this)
    }
    private fun isMainProcess(context: Context): Boolean {
        return context.packageName == getCurrentProcessName(context)
    }

    private fun getCurrentProcessName(context: Context): String? {
        val pid = Process.myPid()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return activityManager.runningAppProcesses
            ?.firstOrNull { it.pid == pid }
            ?.processName
    }
}