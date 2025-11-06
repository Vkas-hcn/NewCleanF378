package gh.sj


import android.annotation.SuppressLint
import android.content.Context

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import android.util.Base64
import android.util.Log
import gh.cark.NcZong
import gh.cark.NcZong.zongApp
import gh.datapost.NcPointFun
import gh.datapost.NcPointPost
import java.io.IOException
import java.util.concurrent.TimeUnit

object NcGoA {

    interface CallbackMy {
        fun onSuccess(response: String)
        fun onFailure(error: String)
    }

    fun showAppVersion(): String {
        return zongApp.packageManager.getPackageInfo(zongApp.packageName, 0).versionName ?: ""
    }


    @SuppressLint("HardwareIds")
    fun adminData(): String {
        val keyIsAndroid = NcZong.aau
        val keyIsRef = NcZong.ifk
        return JSONObject().apply {
            put("dBEQGCr", "com.keep.devicesfresh.ppowerclean")
            put("GIFaGqW", keyIsAndroid)
            put("gcf", keyIsRef)
//            put("gcf", "555")
            put("fOwbi", showAppVersion())
            //referrerClickTimestampSeconds
            put("wEfWcd",NcZong.r_c_t_s)
            //referrerClickTimestampServerSeconds
            put("bEKrBme",NcZong.r_c_t_s_s)
            //installerPackageName
            put("cmRKbr",getISData(zongApp))
        }.toString()
    }

    fun getISData(context: Context):String{
        val installerPackageName: String? = context.packageManager
            .getInstallerPackageName(context.packageName)
        return  installerPackageName?:""
    }

    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    fun postAdminData(callback: CallbackMy) {
        NcZong.showLog("postAdminData=${adminData()}")
        val jsonBodyString = JSONObject(adminData()).toString()
        val timestamp = System.currentTimeMillis().toString()
        val xorEncryptedString = jxData(jsonBodyString, timestamp)
        val base64EncodedString = Base64.encodeToString(
            xorEncryptedString.toByteArray(StandardCharsets.UTF_8),
            Base64.NO_WRAP
        )

        val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
        val requestBody = base64EncodedString.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(NcZong.getAdminUrl())
            .post(requestBody)
            .addHeader("timestamp", timestamp)
            .build()
        NcPointFun.postPointFun(false, "config_R")
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.onFailure("Request failed: ${e.message}")
                NcPointFun.postPointFun(true, "config_G", "getstring","timeout")
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code != 200) {
                    callback.onFailure("Unexpected code $response")
                    NcPointPost.ConfigG("",response.code.toString())
                    return
                }
                try {
                    val timestampResponse = response.header("timestamp")
                        ?: throw IllegalArgumentException("Timestamp missing in headers")

                    val decodedBytes = Base64.decode(response.body?.string() ?: "", Base64.DEFAULT)
                    val decodedString = String(decodedBytes, Charsets.UTF_8)
                    val finalData = jxData(decodedString, timestampResponse)
                    val jsonResponse = JSONObject(finalData)
                    val jsonData = JSONObject(parseAdminRefData(jsonResponse.toString())).toString()
                    ServiceHelper.showFb(
                        NcZong.getKeyTypeValue(jsonData, "f_value"),
                        NcZong.getKeyTypeValue(jsonData, "f_t_vlaue")
                    )
                    Log.e("TAG", "onResponse: ${jsonData}")
                    Log.e("TAG", "onResponse:NcZong.getTypeState(jsonData)=${NcZong.getTypeState(jsonData)} ")
                    NcPointPost.ConfigG(NcZong.getTypeState(jsonData),"200")

                    callback.onSuccess(jsonData)
                } catch (e: Exception) {
                    callback.onFailure("Decryption failed: ${e.message}")
                }
            }
        })

    }

    private fun jxData(text: String, timestamp: String): String {
        val cycleKey = timestamp.toCharArray()
        val keyLength = cycleKey.size
        return text.mapIndexed { index, char ->
            char.toInt().xor(cycleKey[index % keyLength].toInt()).toChar()
        }.joinToString("")
    }

    private fun parseAdminRefData(jsonString: String): String {
        try {
            val confString = JSONObject(jsonString).getJSONObject("dioEQHh").getString("conf")
            return confString
        } catch (e: Exception) {
            return ""
        }
    }

    fun postPutData(body: Any, callbackData: CallbackMy) {
        val jsonBodyString = JSONObject(body.toString()).toString()
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            jsonBodyString
        )

        val request = Request.Builder()
            .url(NcZong.getUpUrl())
            .post(requestBody)
            .addHeader("Content-Type", "application/json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                NcZong.showLog("tba-Error: ${e.message}")
                callbackData.onFailure(e.message ?: "Unknown error")
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        callbackData.onFailure("Unexpected code $response")
                    } else {
                        val responseData = response.body?.string() ?: ""
                        callbackData.onSuccess(responseData)
                    }
                }
            }
        })
    }

}
