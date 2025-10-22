package zj.go.zhid

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Date：2025/10/20
 * Describe: 定期任务Worker，使用enqueueUniquePeriodicWork
 * 用于保持应用进程活跃，防止被后台杀死
 */
class DWPE(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    
    companion object {
        const val WORK_NAME = "bmkads"
        
        /**
         * 启动定期任务，每15分钟执行一次
         */
        fun start(context: Context) {
            val periodicWorkRequest = PeriodicWorkRequestBuilder<DWPE>(
                15, TimeUnit.MINUTES
            ).build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}