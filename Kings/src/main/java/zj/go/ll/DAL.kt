package zj.go.ll

import android.app.Activity
import android.app.Application
import android.os.Bundle
import gh.sj.ServiceHelper
import gh.cark.NcZong


class DAL : Application.ActivityLifecycleCallbacks {
    
    // 前台Activity数量
    private var num = 0
    
    // 保存所有存活的Activity
    private val activityStack = mutableListOf<Activity>()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        ServiceHelper.openNotification(activity)
        // 添加Activity到栈中
        activityStack.add(activity)
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
            // 应用退到后台，检查是否为A方案
            onAppEnteredBackground()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        // 从栈中移除Activity
        activityStack.remove(activity)
    }
    

    private fun onAppEnteredBackground() {
        NcZong.showLog("DAL: 应用退到后台")
        // 检查是否为A方案
        val userType = NcZong.getTypeState(NcZong.akv)
        if (userType == "one") {
            NcZong.showLog("DAL: 检测到A方案用户，准备关闭所有Activity")
            finishAllActivities()
        } else {
            NcZong.showLog("DAL: 非A方案用户（userType=$userType），不关闭Activity")
        }
    }
    

     fun finishAllActivities() {
        try {
            val activitiesToFinish = activityStack.toList()
            

            activitiesToFinish.forEach { activity ->
                if (!activity.isFinishing) {
                    activity.finish()
                }
            }
            
            NcZong.showLog("DAL: 所有Activity已关闭")
        } catch (e: Exception) {
            NcZong.showLog("DAL: 关闭Activity时发生错误 - ${e.message}")
            e.printStackTrace()
        }
    }

}