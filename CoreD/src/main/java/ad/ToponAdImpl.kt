package ad

import android.app.Activity
import com.ak.impI.Core
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import com.thinkup.core.api.AdError
import com.thinkup.core.api.TUAdInfo
import com.thinkup.interstitial.api.TUInterstitial
import com.thinkup.interstitial.api.TUInterstitialListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.apply
import kotlin.collections.set
import kotlin.let
import kotlin.text.isBlank

/**
 * Date：2025/10/13
 * Describe:
 * todo topon 聚合不需要请删除这个类
 */

class ToponAdImpl(val tag: String) : TUInterstitialListener {

    private var isLoading = false
    private var lT = 0L
    private var mAd: TUInterstitial? = null


    // 加载广告
    fun lAd(id: String) {
        if (id.isBlank()) return
        if (isLoading && System.currentTimeMillis() - lT < 61000) return
        if (isReadyAd()) return
        isLoading = true
        lT = System.currentTimeMillis()
        Core.pE("advertise_req$tag")
        mAd = TUInterstitial(Core.mApp, id)
        mAd?.setAdListener(this)
        mAd?.load()
    }

    fun isReadyAd(): Boolean {
        return mAd?.isAdReady == true
    }

    private var call: (() -> Unit)? = null
    private var time = 0L

    // 显示广告
    fun shAd(a: Activity): Boolean {
        val ad = mAd
        if (ad?.isAdReady == true) {
            call = {
                a.finishAndRemoveTask()
                AdE.isSAd = false
            }
            time = System.currentTimeMillis()
            Core.pE("advertise_show")
            ad.show(a)
            mAd = null
            return true
        }
        return false
    }

    override fun onInterstitialAdLoaded() {
        isLoading = false
        Core.pE("advertise_get$tag")
    }

    override fun onInterstitialAdLoadFail(p0: AdError?) {
        isLoading = false
        Core.pE("advertise_fail$tag", "${p0?.code}")
    }

    override fun onInterstitialAdClicked(p0: TUAdInfo?) {}

    override fun onInterstitialAdShow(p0: TUAdInfo?) {
        Core.pE("advertise_show_t", "${(System.currentTimeMillis() - time) / 1000}")
        p0?.let {
            postP(it)
        }
        AdE.adShow()
    }

    override fun onInterstitialAdClose(p0: TUAdInfo?) {
        call?.invoke()
        call = null
    }

    override fun onInterstitialAdVideoStart(p0: TUAdInfo?) {}

    override fun onInterstitialAdVideoEnd(p0: TUAdInfo?) {}

    override fun onInterstitialAdVideoError(p0: AdError?) {
        Core.pE("advertise_fail_api", "${p0?.code}_${p0?.desc}")
        call?.invoke()
        call = null
        AdE.mAdC.loadAd()
    }

    private fun postP(ad: TUAdInfo) {
        Core.postAd(JSONObject().apply {
            put("", ad.publisherRevenue * 1000000)//ad_pre_ecpm
            put("", ad.currency)//currency
            put("", ad.networkName)//ad_network
            put("", "topon")//ad_source_client
            put("", ad.placementId)//ad_code_id
            put("", ad.adsourceId)//ad_pos_id
            put("", ad.format)//ad_format
        }.toString())

        val cpm = ad.publisherRevenue

        // pangle
        val adRevenueData = AFAdRevenueData(
            ad.networkName,  // monetizationNetwork
            MediationNetwork.TOPON,  // mediationNetwork
            "USD",  // currencyIso4217Code
            cpm // revenue
        )

        val additionalParameters: MutableMap<String, Any> = HashMap()
        additionalParameters[AdRevenueScheme.COUNTRY] = ad.country
        additionalParameters[AdRevenueScheme.AD_UNIT] = ad.adsourceId
        additionalParameters[AdRevenueScheme.AD_TYPE] = "i"
        additionalParameters[AdRevenueScheme.PLACEMENT] = ad.placementId
        AppsFlyerLib.getInstance().logAdRevenue(adRevenueData, additionalParameters)

        AdE.postEcpm(cpm)
    }

}