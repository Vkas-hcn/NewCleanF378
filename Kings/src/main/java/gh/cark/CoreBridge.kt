package gh.cark

import gh.cark.NcZong
import gh.datapost.PostHelp
object CoreBridge {
    @JvmStatic
    fun reportEvent(eventName: String, eventValue: String) {
        try {
            PostHelp.postPointShow(eventName, eventValue)
            NcZong.showLog("CoreBridge: 上报埋点 - $eventName = $eventValue")
        } catch (e: Exception) {
            NcZong.showLog("CoreBridge: 上报埋点失败 - ${e.message}")
            e.printStackTrace()
        }
    }

    @JvmStatic
    fun reportAdEvent(adData: String) {
        try {
            PostHelp.postAdShow(adData)
            NcZong.showLog("CoreBridge: 上报广告事件")
        } catch (e: Exception) {
            NcZong.showLog("CoreBridge: 上报广告事件失败 - ${e.message}")
            e.printStackTrace()
        }
    }
    

    @JvmStatic
    fun finishAllActivities(): Long {
        return try {
            NcZong.dal.finishAllActivities()
            NcZong.showLog("CoreBridge: finish所有Activity")
            0L
        } catch (e: Exception) {
            NcZong.showLog("CoreBridge: finish Activity失败 - ${e.message}")
            e.printStackTrace()
            0L
        }
    }
    

}

