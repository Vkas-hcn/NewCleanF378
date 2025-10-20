package com.demo

import android.content.Context
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib

/**
 * Date：2025/10/19
 * Describe:
 */
class AFSdkInit {

    // 传入AndroidId 同TBA的AndroidId和distinct_id。
    fun init(context: Context, androidId: String) {
        // todo modify
        AppsFlyerLib.getInstance().setDebugLog(true)
        // todo modify
        AppsFlyerLib.getInstance()
            .init("i3w87P32U399MCPKjzJmdD", object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) = Unit
                override fun onConversionDataFail(p0: String?) = Unit
                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) = Unit
                override fun onAttributionFailure(p0: String?) = Unit
            }, context)
        AppsFlyerLib.getInstance().setCustomerUserId(androidId)
        AppsFlyerLib.getInstance().start(context)
        AppsFlyerLib.getInstance().logSession(context)
    }
}