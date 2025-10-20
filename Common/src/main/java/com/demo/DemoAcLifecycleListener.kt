package com.demo

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Date：2025/10/19
 * Describe:
 */
class DemoAcLifecycleListener : Application.ActivityLifecycleCallbacks {
    private var num = 0


    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        DemoHelper.openNotification(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        num++
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        num--
        if (num <= 0) {
            num = 0
            // todo 退到后台后关闭所有ac，需要注意的是只在A方案中进行
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}