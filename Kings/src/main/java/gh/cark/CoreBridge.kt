package gh.cark

import gh.cark.NcZong
import gh.datapost.PostHelp
object CoreBridge {
    @JvmStatic
    fun reportEvent(eventName: String, eventValue: String) {
        try {
            PostHelp.postPointShow(eventName, eventValue)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun reportAdEvent(adData: String) {
        try {
            PostHelp.postAdShow(adData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    

    @JvmStatic
    fun finishAllActivities(): Long {
        return try {
            NcZong.dal.finishAllActivities()
            0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
    

}

