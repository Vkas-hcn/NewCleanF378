package ssgo.serconfig

import android.app.Notification
import android.content.Context
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import bgnj.deji.R

/**
 * Date：2025/10/23
 * Describe: 通知构建器
 * 负责创建和配置前台服务通知
 */
internal class NotifyBuilder(private val ctx: Context) {
    
    /**
     * 构建前台服务通知
     */
    fun buildNotification(): Notification {
        val builder = createBaseBuilder()
        configureBuilder(builder)
        attachCustomView(builder)
        return builder.build()
    }
    
    /**
     * 创建基础构建器
     */
    private fun createBaseBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(ctx, FgConfig.CH_ID)
    }
    
    /**
     * 配置构建器属性
     */
    private fun configureBuilder(builder: NotificationCompat.Builder) {
        builder.apply {
            // 禁用自动取消
            setAutoCancel(false)
            // 设置持续运行
            setOngoing(true)
            // 只提醒一次
            setOnlyAlertOnce(true)
            // 设置内容
            setContentTitle(FgConfig.NOTIFY_TITLE)
            setContentText(FgConfig.NOTIFY_TEXT)
            // 设置小图标
            setSmallIcon(R.drawable.tm_ic)
            // 设置分类
            setCategory(Notification.CATEGORY_SERVICE)
            // 设置优先级
            priority = NotificationCompat.PRIORITY_MIN
            // 禁用声音和震动
            setSound(null)
            setVibrate(null)
        }
    }
    
    /**
     * 附加自定义视图
     */
    private fun attachCustomView(builder: NotificationCompat.Builder) {
        val customView = createCustomView()
        builder.setCustomContentView(customView)
    }
    
    /**
     * 创建自定义视图
     */
    private fun createCustomView(): RemoteViews {
        return RemoteViews(ctx.packageName, R.layout.xxm_k)
    }
}

