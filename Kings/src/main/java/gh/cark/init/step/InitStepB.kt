package gh.cark.init.step

import android.app.Application
import android.util.Log
import com.bytedance.sdk.openadsdk.api.PAGMUserInfoForSegment
import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.bytedance.sdk.openadsdk.api.init.PAGMConfig
import com.bytedance.sdk.openadsdk.api.init.PAGMSdk
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.thinkup.core.api.TUSDK
import gh.cark.NcZong


internal object InitStepB {
    

    fun execute(app: Application) {
        initializeToponAd(app)
    }
    

     fun initializePangleAd(app: Application,ref: String) {
        val adConfig = createPangleConfig(ref)
        executePangleInit(app, adConfig)
    }
    

    private fun createPangleConfig(ref: String): PAGMConfig {
        val appId = retrievePangleAppId()
        val channel = getChannelFromRef(ref)

        return PAGMConfig.Builder()
            .appId(appId)
            .setConfigUserInfoForSegment(
                PAGMUserInfoForSegment.Builder()
                    .setChannel(channel)
                    .build()
            )
            .supportMultiProcess(false)
            .build()
    }

    private fun getChannelFromRef(ref: String): String {
        return try {
            val refLowerCase = ref.lowercase()
            when {
                refLowerCase.contains("facebook") || refLowerCase.contains("fb4a") -> {
                    "facebook"
                }

                refLowerCase.contains("tiktok") || refLowerCase.contains("bytedance") -> {
                    "tiktok"
                }

                refLowerCase.contains("gclid") -> {
                    "GoogleAds"
                }

                else -> {
                    "organic"
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "unknown"
        }
    }
    private fun retrievePangleAppId(): String {
        return NcZong.getPangKey()
    }
    

    private fun executePangleInit(app: Application, config: PAGMConfig) {
        runCatching {
            PAGMSdk.init(app, config, null)
            Log.e("TAG", "initialization ad sdk success: ", )
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
    

    private fun initializeToponAd(application: Application) {
        val appId = retrieveToponAppId()
        val appKey = retrieveToponAppKey()
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

