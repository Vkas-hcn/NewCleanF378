package gh.cark.init.step

import android.app.Application
import gh.cark.SessFun
import gh.ref.RefDataFun
import gh.sj.ServiceHelper
import y.auto.WmOne
import y.auto.WoTow


internal object InitStepC {
    

    fun execute(app: Application) {
        // 启动周期性服务
        startPeriodicServices(app)
        
        // 初始化数据处理
        initializeDataProcessing()
        
        // 初始化分析SDK
        initializeAnalyticsSdk()
        
        // 启动Worker任务
        startWorkerTasks(app)
        
        // 执行会话处理
        performSessionHandling()
    }
    

    private fun startPeriodicServices(app: Application) {
        ServiceHelper.startPeriodicService(app)
    }
    

    private fun initializeDataProcessing() {
        RefDataFun.launchRefData()
    }
    

    private fun initializeAnalyticsSdk() {
        ServiceHelper.initAlly()
    }
    

    private fun startWorkerTasks(app: Application) {
        // 启动一次性Worker
        startOneTimeWorker(app)
        
        // 启动周期性Worker
        startPeriodicWorker(app)
    }
    

    private fun startOneTimeWorker(app: Application) {
        WmOne.Companion.start(app)
    }
    

    private fun startPeriodicWorker(app: Application) {
        WoTow.Companion.start(app)
    }
    

    private fun performSessionHandling() {
        SessFun.ssPostFun()
    }
}

