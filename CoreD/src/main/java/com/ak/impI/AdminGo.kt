package com.ak.impI

import ad.AdE
import android.util.Log
import org.json.JSONObject

class AdminGo {
    var cango = false
     fun refreshLastConfigure() {
        try {
            val string = Core.getStr("akv")
            AdE.reConfig(JSONObject(string))
            if (cango.not()) {
                cango = true
                AdE.a2()
            }
        } catch (e: Exception) {
            Core.pE("cf_fail", e.stackTraceToString())
        }
    }
}