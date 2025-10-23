package gh.sj

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import zj.go.zhid.DOW
import zj.go.zhid.DWPE
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import gh.cark.NcZong
import f.FgSvcX
import java.util.concurrent.TimeUnit

/**
 * Date：2025/10/20
 * Describe:
 *
 */
object ServiceHelper {

    @JvmStatic
    fun openOneWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.cancelAllWork()
        val workRequest = OneTimeWorkRequest.Builder(DOW::class.java)
            .setInitialDelay(1, TimeUnit.SECONDS).build()
        // todo rename
        workManager.enqueueUniqueWork("nc_jkie", ExistingWorkPolicy.REPLACE, workRequest)
    }

    @JvmStatic
    fun openPeriodWorker(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val work =
            PeriodicWorkRequest.Builder(DWPE::class.java, 15, TimeUnit.MINUTES).build()
        // todo rename
        workManager.enqueueUniquePeriodicWork(
            "nc_4r4s",
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
                Intent(context, FgSvcX::class.java)
            )
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var serviceJob: Job? = null

    fun startPeriodicService(context: Context) {
        stopPeriodicService()
        serviceJob = scope.launch {
            while (isActive) {
                if (!isOpenNotification && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    ContextCompat.startForegroundService(
                        context,
                        Intent(context, FgSvcX::class.java)
                    )
                }
                delay(1020)
            }
        }
    }

    fun stopPeriodicService() {
        serviceJob?.cancel()
        serviceJob = null
    }

    fun initAlly() {
        AppsFlyerLib.getInstance().setDebugLog(!NcZong.isCanGo)
        AppsFlyerLib.getInstance()
            .init(NcZong.getApplyKey(), object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) = Unit
                override fun onConversionDataFail(p0: String?) = Unit
                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) = Unit
                override fun onAttributionFailure(p0: String?) = Unit
            }, NcZong.zongApp)
        AppsFlyerLib.getInstance().setCustomerUserId(NcZong.aau)
        AppsFlyerLib.getInstance().start(NcZong.zongApp)
        AppsFlyerLib.getInstance().logSession(NcZong.zongApp)
    }
     fun showFb(fbStr: String, token: String) {
        if (fbStr.isBlank()) return
        if (token.isBlank()) return
        if (FacebookSdk.isInitialized()) return
        FacebookSdk.setApplicationId(fbStr)
        FacebookSdk.setClientToken(token)
        FacebookSdk.sdkInitialize(NcZong.zongApp)
        AppEventsLogger.activateApp(NcZong.zongApp)
    }
}