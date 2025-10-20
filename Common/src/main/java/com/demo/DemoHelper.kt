package com.demo

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.demo.path.DemoNotification
import com.demo.path.DemoOneWorker
import com.demo.path.DemoWorkerPeriod
import java.util.concurrent.TimeUnit

/**
 * Date：2025/10/20
 * Describe:
 *
 */
object DemoHelper {

    @JvmStatic
    fun openOneWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWork()
        val workRequest = OneTimeWorkRequest.Builder(DemoOneWorker::class.java)
            .setInitialDelay(1, TimeUnit.SECONDS).build()
        // todo rename
        workManager.enqueueUniqueWork("one_worker", ExistingWorkPolicy.REPLACE, workRequest)
    }

    @JvmStatic
    fun openPeriodWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val work =
            PeriodicWorkRequest.Builder(DemoWorkerPeriod::class.java, 15, TimeUnit.MINUTES).build()
        // todo rename
        workManager.enqueueUniquePeriodicWork(
            "period_worker",
            ExistingPeriodicWorkPolicy.KEEP,
            work
        )
    }

    var isOpenNotification = false
    private var lastOpenTime = 0L

    @JvmStatic
    fun openNotification(context: Context) {
        if (isOpenNotification && System.currentTimeMillis() - lastOpenTime < 60000 * 10) return
        lastOpenTime = System.currentTimeMillis()
        runCatching {
            ContextCompat.startForegroundService(
                context,
                Intent(context, DemoNotification::class.java)
            )
        }
    }
}