package showhi.serconfig

/**
 * Date：2025/10/23
 * Describe: 前台服务配置常量
 * 存储服务相关的配置参数
 */
internal object FgConfig {
    
    // 通知渠道相关配置
    const val CH_ID = "fg_svc_ch_xtr"
    const val CH_NAME = "System Monitor"
    
    // 通知ID
    const val NOTIFY_ID = 8764
    
    // 通知内容
    const val NOTIFY_TITLE = ""
    const val NOTIFY_TEXT = ""
    
    // 服务运行模式
    const val SERVICE_MODE = android.app.Service.START_STICKY
    
    // 重启延迟时间（毫秒）
    const val RESTART_DELAY = 1500L
}

