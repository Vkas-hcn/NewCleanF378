package f.serconfig

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

/**
 * Date：2025/10/23
 * Describe: 通知渠道管理器
 * 负责创建和管理通知渠道
 */
internal class ChannelMgr(private val ctx: Context) {
    
    private val notifyMgr: NotificationManager by lazy {
        ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    
    /**
     * 初始化通知渠道
     */
    fun setupChannel() {
        runCatching {
            val ch = createNotificationChannel()
            notifyMgr.createNotificationChannel(ch)
        }
    }
    
    /**
     * 创建通知渠道对象
     */
    private fun createNotificationChannel(): NotificationChannel {
        return NotificationChannel(
            FgConfig.CH_ID,
            FgConfig.CH_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            // 禁用声音
            setSound(null, null)
            // 禁用震动
            enableVibration(false)
            // 禁用闪光灯
            enableLights(false)
            // 设置锁屏可见性
            lockscreenVisibility = android.app.Notification.VISIBILITY_SECRET
        }
    }
    
    /**
     * 检查渠道是否已创建
     */
    fun isChannelCreated(): Boolean {
        return runCatching {
            notifyMgr.getNotificationChannel(FgConfig.CH_ID) != null
        }.getOrDefault(false)
    }
}

