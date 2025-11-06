package y.auto

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit


class WmOne(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    
    companion object {
        const val WORK_NAME = "bmgkls"
        

        fun start(context: Context) {
            val workRequest = OneTimeWorkRequestBuilder<WmOne>()
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
            scheduleNextWork()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
    
    private fun scheduleNextWork() {
        val nextWorkRequest = OneTimeWorkRequestBuilder<WmOne>()
            .setInitialDelay(15, TimeUnit.MINUTES)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            nextWorkRequest
        )
    }
}