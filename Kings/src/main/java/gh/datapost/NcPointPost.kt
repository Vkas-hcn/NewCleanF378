package gh.datapost

import android.content.pm.PackageManager
import android.os.Build
import org.json.JSONObject
import java.util.UUID
import gh.cark.NcZong
import gh.sj.NcGoA
import kotlinx.serialization.json.JsonObject

object NcPointPost {
    private fun topJsonData(): JSONObject {
        return JSONObject().apply {
            put("s", JSONObject().apply {
                //os
                put("topeka", "ephesian")
                //device_model-最新需要传真实值
                put("beadle", Build.BRAND)
                //distinct_id
                put("showboat", NcZong.aau)
                //bundle_id
                put("empiric", NcZong.zongApp.packageName)
                //manufacturer
                put("lingual", Build.MANUFACTURER)
                //operator 传假值字符串
                put("topsy", "rvvs")
                //os_version
                put("crucify", Build.VERSION.RELEASE)
                //app_version
                put("dwyer", NcGoA.showAppVersion())
                //log_id
                put("power", UUID.randomUUID().toString())
                //client_ts
                put("hansen", System.currentTimeMillis())
                //gaid
                put("hamburg", "")
                //system_language//假值
                put("orphan", "ggty_asd")
                //android_id
                put("leer", NcZong.aau)
            })
        }

    }

    fun upInstallJson(): String {
        return topJsonData().apply {
            put("twain", JSONObject().apply {
                //build
                put("how", "build/${Build.ID}")

                //referrer_url
                put("copeland", NcZong.ifk)

                //user_agent
                put("bowen", "")

                //lat
                put("phenol", "compute")

                //referrer_click_timestamp_seconds
                put("bead", 0)

                //install_begin_timestamp_seconds
                put("entendre", 0)

                //referrer_click_timestamp_server_seconds
                put("ideology", 0)

                //install_begin_timestamp_server_seconds
                put("moot", 0)

                //install_first_seconds
                put("varian", getFirstInstallTime())

                //last_update_seconds
                put("saucy", 0)
            })
        }.toString()
    }

    fun upAdJson(adJson: String): String {
        val adJsonObject = JSONObject(adJson)
        return topJsonData().apply {
            val keys = adJsonObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                put(key, adJsonObject.get(key))
            }
            put("hermite","gland")
        }.toString()
    }



    fun upPointJson(
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null,
    ): String {
        return topJsonData().apply {
            put("hermite", name)
            if (key1 != null) {
                put(key1 + "_mastic", keyValue1)
            }
            if (key2 != null) {
                put(key2 + "_mastic", keyValue2)
            }
        }.toString()
    }

    private fun getFirstInstallTime(): Long {
        try {
            val packageInfo =
                NcZong.zongApp.packageManager.getPackageInfo(NcZong.zongApp.packageName, 0)
            return packageInfo.firstInstallTime / 1000
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return 0
    }

    fun ConfigG(typeUser: String, codeInt: String?) {
        var isuserData: String? = null
        isuserData = if (codeInt == null) {
            null
        } else if (codeInt != "200") {
            codeInt
        } else if (typeUser == "one") {
            "a"
        } else {
            "b"
        }
        NcPointFun.postPointFun(true, "config_G", "getstring", isuserData)
    }

}