package showhi.rf.ghht.hj

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.IBinder
import showhi.serconfig.ChannelMgr
import showhi.serconfig.FgConfig
import showhi.serconfig.NotifyBuilder
import showhi.serconfig.SvcStateMgr
import gh.sj.ServiceHelper


class FgSvcX : Service() {

    // 通知渠道管理器
    private var channelMgr: ChannelMgr? = null

    // 通知对象
    private var notify: Notification? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        initializeService()
    }

    /**
     * 初始化服务组件
     */
    private fun initializeService() {
        // 创建并设置通知渠道
        setupNotificationChannel()

        // 构建通知对象
        buildServiceNotification()

        // 更新外部状态标记
        updateExternalState(true)
    }

    /**
     * 设置通知渠道
     */
    private fun setupNotificationChannel() {
        channelMgr = ChannelMgr(this).apply {
            setupChannel()
        }
    }

    /**
     * 构建服务通知
     */
    private fun buildServiceNotification() {
        notify = NotifyBuilder(this).buildNotification()
    }

    /**
     * 更新外部状态
     */
    private fun updateExternalState(isRunning: Boolean) {
        ServiceHelper.isOpenNotification = isRunning
        if (isRunning) {
            SvcStateMgr.markAsStarted()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService()
        return getForegroundServiceMode()
    }

    /**
     * 启动前台服务
     */
    private fun startForegroundService() {
        runCatching {
            notify?.let { notification ->
                startForeground(FgConfig.NOTIFY_ID, notification)
            }
        }.onFailure { throwable ->
            // 静默处理错误，避免崩溃
            throwable.printStackTrace()
        }
    }

    /**
     * 获取服务运行模式
     */
    private fun getForegroundServiceMode(): Int {
        return FgConfig.SERVICE_MODE
    }

    override fun onDestroy() {
        cleanupService()
        super.onDestroy()
    }

    /**
     * 清理服务资源
     */
    private fun cleanupService() {
        // 更新外部状态
        updateExternalState(false)

        // 标记服务已停止
        SvcStateMgr.markAsStopped()

        // 清理引用
        notify = null
        channelMgr = null
    }
}