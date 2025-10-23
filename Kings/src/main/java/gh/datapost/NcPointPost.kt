package gh.datapost

import android.content.pm.PackageManager
import android.os.Build
import org.json.JSONObject
import java.util.UUID
import gh.cark.NcZong
import gh.sj.NcGoA

object NcPointPost {
    private fun topJsonData(): JSONObject {
        return JSONObject().apply {
            //bundle_id
            put("flora", NcZong.zongApp.packageName)
            //client_ts
            put("visit", System.currentTimeMillis())
            //app_version
            put("creep", NcGoA.showAppVersion())
            //android_id
            put("stasis", NcZong.aau)
            //operator 传假值字符串
            put("rove", "rvvs")
            //device_model-最新需要传真实值
            put("barbour", Build.BRAND)
            //os_version
            put("pleural", Build.VERSION.RELEASE)
            //manufacturer
            put("joel", Build.MANUFACTURER)
            //log_id
            put("kiosk", UUID.randomUUID().toString())
            //distinct_id
            put("berg", NcZong.aau)
            //os
            put("ruthless", "absent")
            //system_language//假值
            put("anatomic", "ggty_asd")
            //gaid
            put("greet", "")

        }

    }

    fun upInstallJson(): String {
        return topJsonData().apply {
            //build
            put("lao", "build/${Build.ID}")

            //referrer_url
            put("deportee", NcZong.ifk)

            //user_agent
            put("aviary", "")

            //lat
            put("agee", "brett")

            //referrer_click_timestamp_seconds
            put("elope", 0)

            //install_begin_timestamp_seconds
            put("maltese", 0)

            //referrer_click_timestamp_server_seconds
            put("neurotic", 0)

            //install_begin_timestamp_server_seconds
            put("mann", 0)

            //install_first_seconds
            put("hartford", getFirstInstallTime())

            //last_update_seconds
            put("spinal", 0)

            put("glue", "knick")
        }.toString()
    }

    fun upAdJson(adJson: String): String {
        return topJsonData().apply {
            put("daemon", JSONObject(adJson))
        }.toString()
    }

    fun upPointJson(name: String): String {
        return topJsonData().apply {
            put("glue", name)
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
            put("glue", name)
            put(name, JSONObject().apply {
                if (key1 != null) {
                    put(key1 + "_omaha", keyValue1)
                }
                if (key2 != null) {
                    put(key2 + "_omaha", keyValue2)
                }
            })
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