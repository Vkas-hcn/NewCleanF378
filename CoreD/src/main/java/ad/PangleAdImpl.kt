package ad

import android.app.Activity
import android.os.Bundle
import com.ak.impI.Core
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAd
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdInteractionCallback
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialAdLoadCallback
import com.bytedance.sdk.openadsdk.api.interstitial.PAGInterstitialRequest
import com.bytedance.sdk.openadsdk.api.model.PAGAdEcpmInfo
import com.bytedance.sdk.openadsdk.api.model.PAGErrorModel
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import java.util.Currency


/**
 * Date：2025/7/10
 * Describe:
 */
class PangleAdImpl(val t: String = "") {
    private var isL = false
    private var lT = 0L
    private var mAd: PAGInterstitialAd? = null

    fun lAd(id: String) {
        if (id.isBlank()) return
        if (isL && System.currentTimeMillis() - lT < 61000) return
        if (mAd != null) return
        isL = true
        lT = System.currentTimeMillis()
        Core.pE("advertise_req$t")
        PAGInterstitialAd.loadAd(
            id,
            PAGInterstitialRequest(Core.mApp),
            object : PAGInterstitialAdLoadCallback {
                override fun onError(pagErrorModel: PAGErrorModel) {
                    isL = false
                    Core.pE(
                        "advertise_fail$t",
                        "${pagErrorModel.errorCode}_${pagErrorModel.errorMessage}"
                    )
                }

                override fun onAdLoaded(pagInterstitialAd: PAGInterstitialAd) {
                    mAd = pagInterstitialAd
                    isL = false
                    Core.pE("advertise_get$t")
                }
            })
    }

    fun isReadyAd(): Boolean {
        return mAd?.isReady == true
    }

    fun shAd(a: Activity): Boolean {
        val ad = mAd
        if (ad != null) {
            val time = System.currentTimeMillis()
            Core.pE("advertise_show")
            ad.setAdInteractionCallback(object : PAGInterstitialAdInteractionCallback() {
                override fun onAdReturnRevenue(pagAdEcpmInfo: PAGAdEcpmInfo?) {
                    super.onAdReturnRevenue(pagAdEcpmInfo)
                    Core.pE("advertise_show_t", "${(System.currentTimeMillis() - time) / 1000}")
                    pagAdEcpmInfo?.let {
                        val adRevenueData = AFAdRevenueData(
                            it.adnName,  // monetizationNetwork
                            MediationNetwork.CUSTOM_MEDIATION,  // mediationNetwork
                            "USD",  // currencyIso4217Code
                            it.cpm.toDouble() / 1000 // revenue
                        )
                        val additionalParameters: MutableMap<String, Any> = HashMap()
                        additionalParameters[AdRevenueScheme.COUNTRY] = it.country
                        additionalParameters[AdRevenueScheme.AD_UNIT] = it.adUnit
                        additionalParameters[AdRevenueScheme.AD_TYPE] = "i"
                        additionalParameters[AdRevenueScheme.PLACEMENT] = it.placement
                        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)
                        postValue(it)
                    }
                    AdE.adShow()
                }

                override fun onAdDismissed() {
                    super.onAdDismissed()
                    a.finishAndRemoveTask()
                    AdE.isSAd = false
                }

                override fun onAdShowFailed(pagErrorModel: PAGErrorModel) {
                    super.onAdShowFailed(pagErrorModel)
                    a.finishAndRemoveTask()
                    Core.pE(
                        "advertise_fail_api",
                        "${pagErrorModel.errorCode}_${pagErrorModel.errorMessage}"
                    )
                    AdE.isSAd = false
                    AdE.mAdC.loadAd()
                }
            })
            ad.show(a)
            mAd = null
            return true
        }
        return false
    }


    private fun postValue(si: PAGAdEcpmInfo) {
        // todo TBA上报广告 只需要填写TBA文档上的参数
        Core.postAd(
            JSONObject().put("", si.cpm.toDouble() * 1000)//ad_pre_ecpm
                .put("", "USD")//currency
                .put("", si.adnName)//ad_network
                .put("", "pangle")//ad_source_client
                .put("", si.placement)//ad_code_id
                .put("", si.adUnit)//ad_pos_id
                .put("", si.adFormat)//ad_format
                .toString()
        )
        val cpm = si.cpm.toDouble() / 1000
        AdE.postEcpm(cpm)
    }

}