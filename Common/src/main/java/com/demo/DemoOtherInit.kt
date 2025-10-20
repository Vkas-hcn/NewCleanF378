package com.demo

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.google.firebase.messaging.messaging

/**
 * Date：2025/10/19
 * Describe:
 */
class DemoOtherInit {
    // 需要数据持久化
    private var isInitSuccess = false
    fun initOther(context: Context) {
        //FCM
        if (isInitSuccess) return
        // todo modify name 同时把这个名字填到需求文档中
        Firebase.messaging.subscribeToTopic("topic").addOnSuccessListener {
            isInitSuccess = true
        }
    }
    // 显示桌面Icon
    // 这个方法需要传入给钟哥so中隐藏launcher icon的路径，
    // App启动后调用一次，生命周期中只调用一次，注意隐藏一下这个方法不要暴露的在太外面
    // todo modify

    fun enableAlias(alias: String, context: Context) {
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            ComponentName(context, alias),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    // 广告Sdk初始化自行查阅官方文档
    fun adSdkInit(context: Context) {

    }
}