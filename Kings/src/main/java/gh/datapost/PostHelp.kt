package gh.datapost

object PostHelp {
    fun postPointShow(key:String,keyValue: String) {
        val canRetry = when(key) {
            "config_G" -> true
            "cf_fail"-> true
            "pop_fail"-> true
            "advertise_limit"-> true
            else -> false
        }
        NcPointFun.postPointFun(canRetry, key, keyValue)
    }

    fun postAdShow(keyValueJson: String) {
        NcPointFun.postAdFun(keyValueJson)
    }
}