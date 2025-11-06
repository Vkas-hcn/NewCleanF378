package ssgo.serconfig

import java.util.concurrent.atomic.AtomicBoolean

/**
 * Date：2025/10/23
 * Describe: 服务状态管理器
 * 负责管理服务的运行状态和生命周期
 */
internal object SvcStateMgr {
    
    // 服务是否正在运行
    private val isRunning = AtomicBoolean(false)
    
    // 最后启动时间
    private var lastStartTime = 0L
    
    /**
     * 标记服务已启动
     */
    fun markAsStarted() {
        isRunning.set(true)
        lastStartTime = System.currentTimeMillis()
    }
    
    /**
     * 标记服务已停止
     */
    fun markAsStopped() {
        isRunning.set(false)
    }
    
    /**
     * 检查服务是否正在运行
     */
    fun isServiceRunning(): Boolean {
        return isRunning.get()
    }
    
    /**
     * 获取服务运行时长（毫秒）
     */
    fun getRunningDuration(): Long {
        return if (isRunning.get()) {
            System.currentTimeMillis() - lastStartTime
        } else {
            0L
        }
    }
    
    /**
     * 重置状态
     */
    fun reset() {
        isRunning.set(false)
        lastStartTime = 0L
    }
}

