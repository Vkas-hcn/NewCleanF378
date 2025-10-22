package zj.go.zhid

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Date：2025/10/20
 * Describe: 普通任务Worker，使用enqueueUniqueWork，任务完成后启动下一个任务进行循环
 * 用于保持应用进程活跃，防止被后台杀死
 */
class DOW(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    
    companion object {
        const val WORK_NAME = "bmgkls"
        
        /**
         * 启动循环任务
         */
        fun start(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<DOW>()
                .setInitialDelay(15, TimeUnit.MINUTES)
                .build()
                
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
    }
    
    override suspend fun doWork(): Result {
        return try {
            // 任务完成后，启动下一个任务进行循环
            scheduleNextWork()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private fun scheduleNextWork() {
        // 启动下一个任务，延迟15分钟
        val nextWorkRequest = OneTimeWorkRequestBuilder<DOW>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()
            
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            nextWorkRequest
        )
    }
}