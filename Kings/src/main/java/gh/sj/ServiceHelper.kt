package gh.sj

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
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
import kotlinx.coroutines.GlobalScope
import showhi.rf.ghht.hj.FgSvcX

/**
 * Date：2025/10/20
 * Describe:
 *
 */
object ServiceHelper {
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
        Log.e("TAG", "initAlly: id=${NcZong.aau}---${NcZong.getApplyKey()}", )
        AppsFlyerLib.getInstance()
            .init(NcZong.getApplyKey(), null, NcZong.zongApp)
        AppsFlyerLib.getInstance().setCustomerUserId(NcZong.aau)
        AppsFlyerLib.getInstance().start(NcZong.zongApp)
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