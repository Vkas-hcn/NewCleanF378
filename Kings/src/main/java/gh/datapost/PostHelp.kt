package gh.datapost

import android.util.Log

object PostHelp {
    fun postPointShow(name:String,keyValue: String) {
        val canRetry = when(name) {
            "config_G" -> true
            "cf_fail"-> true
            "pop_fail"-> true
            "advertise_limit"-> true
            else -> false
        }
        val key = when(name) {
            "ad_pass" -> "string"
            else -> null
        }
        if(key == "ad_pass"){
            Log.e("TAG", "postPointShow-ad_pass: $keyValue", )
        }
        NcPointFun.postPointFun(canRetry, name, "string",keyValue)
    }

    fun postAdShow(keyValueJson: String) {
        NcPointFun.postAdFun(keyValueJson)
    }
}