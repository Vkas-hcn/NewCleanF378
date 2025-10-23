package gh.cark.init.step

import android.app.Application
import android.util.Log
import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.bytedance.sdk.openadsdk.api.init.PAGMConfig
import com.bytedance.sdk.openadsdk.api.init.PAGMSdk
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.thinkup.core.api.TUSDK
import gh.cark.NcZong


internal object InitStepB {
    

    fun execute(app: Application) {
        // 初始化Pangle广告SDK
        initializePangleAd(app)
        
        // 初始化Topon广告SDK
        initializeToponAd()
    }
    

    private fun initializePangleAd(app: Application) {
        val adConfig = createPangleConfig()
        executePangleInit(app, adConfig)
    }
    

    private fun createPangleConfig(): PAGMConfig {
        val appId = retrievePangleAppId()
        NcZong.showLog("AppConfigManager.appIdPangle=$appId")
        
        return PAGMConfig.Builder()
            .appId(appId)
            .supportMultiProcess(false)
            .build()
    }
    

    private fun retrievePangleAppId(): String {
        return NcZong.getPangKey()
    }
    

    private fun executePangleInit(app: Application, config: PAGMConfig) {
        runCatching {
            PAGMSdk.init(app, config, null)
        }.onFailure { error ->
            NcZong.showLog("Ad SDK initialization failed: ${error.message}")
        }
    }
    

    private fun createPangleCallback(): PAGSdk.PAGInitCallback {
        return object : PAGSdk.PAGInitCallback {
            override fun success() {
                handlePangleSuccess()
            }
            
            override fun fail(code: Int, msg: String) {
                handlePangleFailure(code, msg)
            }
        }
    }
    

    private fun handlePangleSuccess() {
        Log.e("TAG", "PAGInitCallback new api init success")
    }
    

    private fun handlePangleFailure(code: Int, @Suppress("UNUSED_PARAMETER") msg: String) {
        Log.e("TAG", "PAGInitCallback new api init fail: $code")
    }
    

    private fun initializeToponAd() {
        val appId = retrieveToponAppId()
        val appKey = retrieveToponAppKey()
        val application = NcZong.zongApp
        
        executeToponInit(application, appId, appKey)
    }
    

    private fun retrieveToponAppId(): String {
        return NcZong.getToPonAppId()
    }
    

    private fun retrieveToponAppKey(): String {
        return NcZong.getToPonAppKey()
    }

    private fun executeToponInit(app: Application, appId: String, appKey: String) {
        TUSDK.init(app, appId, appKey)
    }
}

