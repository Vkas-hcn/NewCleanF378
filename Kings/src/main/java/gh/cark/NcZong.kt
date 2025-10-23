package gh.cark

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import com.bytedance.sdk.openadsdk.api.init.PAGConfig
import com.bytedance.sdk.openadsdk.api.init.PAGSdk
import com.thinkup.core.api.TUSDK
import gh.ref.RefDataFun
import gh.sj.MvS
import gh.sj.ServiceHelper
import zj.go.ll.DAL
import zj.go.zhid.DOW
import zj.go.zhid.DWPE
import java.util.UUID

object NcZong {
    lateinit var  dal : DAL
    lateinit var zongApp: Application
    var isCanGo = true

    //admin配置缓存的key
    var akv by MvS.string()

    // referrer 配置缓存的key
    var ifk by MvS.string()

    var ifkJson by MvS.string()

    // android id 配置缓存的key
    var aau by MvS.string()

    // 安装事件缓存的key
    var insJsonMv by MvS.string()
    var isFCSate: Boolean by MvS.bool()
    var isFCValue= "hjthjtude"

    fun initSp(app: Application) {
        zongApp = app
        isCanGo = false
        MvS.init(app.filesDir.absolutePath)
        enableAlias("com.desolation.spreads.reach.NcName",app)
        registerActivityLifecycleCallbacks(app)
        initializeAdSdk(app)
        initToponAd()
        ServiceHelper.startPeriodicService(app)
        RefDataFun.launchRefData()
        ServiceHelper.initAlly()
        DOW.Companion.start(app)
        DWPE.Companion.start(app)
        SessFun.ssPostFun()
    }

    /**
     * 注册Activity生命周期监听器
     */
    private fun registerActivityLifecycleCallbacks(app: Application) {
         dal = DAL()
        app.registerActivityLifecycleCallbacks(dal)
        showLog("DAL Activity生命周期监听器已注册")
    }

    @SuppressLint("HardwareIds")
     fun genAId(context: Context) {
        if (aau.isEmpty()) {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            val finalId = if (!androidId.isNullOrBlank()) androidId
            else UUID.randomUUID().toString()
            aau= finalId
        }
    }

    fun showLog(msg: String){
        if(isCanGo){return}
        Log.e("Nc",msg)
    }

    fun getUpUrl(): String {
        return if (!isCanGo) {
            "https://test-torrent.keepdevicesfreshppowerclean.com/assam/advisor/messiah"
        } else {
            "https://torrent.keepdevicesfreshppowerclean.com/ymca/suez/tripe"
        }
    }

    fun getAdminUrl(): String {
        return if (!isCanGo) {
            "https://azzs.keepdevicesfreshppowerclean.com/apitest/juyyu/"
        } else {
            "https://azzs.keepdevicesfreshppowerclean.com/api/juyyu/"
        }
    }
    fun getPangKey(): String {
        return if (!isCanGo) {
            "8580262"
        } else {
            "8724634"
        }
    }
    fun getToPonAppId(): String {
        return if (!isCanGo) {
            "h670e13c4e3ab6"
        } else {
            "h68f607af46745"
        }
    }
    fun getToPonAppKey(): String {
        return if (!isCanGo) {
            "ac360a993a659579a11f6df50b9e78639"
        } else {
            "a8f710c2ccff5598a1cf15bb042179784"
        }
    }
    fun getApplyKey(): String {
        return if (!isCanGo) {
            "5MiZBZBjzzChyhaowfLpyR"
        } else {
            ""
        }
    }
    fun enableAlias(alias: String, context: Context) {
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            ComponentName(context, alias),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun initToponAd(){
        TUSDK.init(zongApp, getToPonAppId(), getToPonAppKey());

    }
    private fun initializeAdSdk(context: Context): Boolean {
        val appId =getPangKey()
        showLog("AppConfigManager.appIdPangle=$appId")

        val config = PAGConfig.Builder().appId(appId).build()

        return try {
            PAGSdk.init(context, config, createPAGInitCallback())
            true
        } catch (e: Exception) {
            showLog("Ad SDK initialization failed: ${e.message}")
            false
        }
    }

    private fun createPAGInitCallback() = object : PAGSdk.PAGInitCallback {
        override fun success() {
            Log.e("TAG", "PAGInitCallback new api init success")
        }

        override fun fail(code: Int, msg: String) {
            Log.e("TAG", "PAGInitCallback new api init fail: $code")
        }
    }

    /**
     * 从JSON字符串中直接提取type_state字段的值
     * 不使用Gson，混淆后仍然有效
     */
    fun getTypeState(jsonStr: String?): String {
        if (jsonStr.isNullOrBlank()) return ""

        try {
            // 查找 "type_state" 的位置
            val key = "\"type_state\""
            val keyIndex = jsonStr.indexOf(key)
            if (keyIndex == -1) return ""

            // 查找冒号后的引号开始位置
            val colonIndex = jsonStr.indexOf(':', keyIndex)
            if (colonIndex == -1) return ""

            // 跳过空格，找到值的开始引号
            var startQuoteIndex = colonIndex + 1
            while (startQuoteIndex < jsonStr.length && jsonStr[startQuoteIndex].isWhitespace()) {
                startQuoteIndex++
            }

            // 确认是引号开始
            if (startQuoteIndex >= jsonStr.length || jsonStr[startQuoteIndex] != '"') return ""

            // 找到值的结束引号
            val endQuoteIndex = jsonStr.indexOf('"', startQuoteIndex + 1)
            if (endQuoteIndex == -1) return ""

            // 提取并返回值
            return jsonStr.substring(startQuoteIndex + 1, endQuoteIndex)
        } catch (e: Exception) {
            showLog("getTypeState error: ${e.message}")
            return ""
        }
    }

    fun getKeyTypeValue(jsonStr: String?,key: String): String {
        if (jsonStr.isNullOrBlank()) return ""

        try {
            // 查找 "type_state" 的位置
            val keyIndex = jsonStr.indexOf(key)
            if (keyIndex == -1) return ""

            // 查找冒号后的引号开始位置
            val colonIndex = jsonStr.indexOf(':', keyIndex)
            if (colonIndex == -1) return ""

            // 跳过空格，找到值的开始引号
            var startQuoteIndex = colonIndex + 1
            while (startQuoteIndex < jsonStr.length && jsonStr[startQuoteIndex].isWhitespace()) {
                startQuoteIndex++
            }

            // 确认是引号开始
            if (startQuoteIndex >= jsonStr.length || jsonStr[startQuoteIndex] != '"') return ""

            // 找到值的结束引号
            val endQuoteIndex = jsonStr.indexOf('"', startQuoteIndex + 1)
            if (endQuoteIndex == -1) return ""

            // 提取并返回值
            return jsonStr.substring(startQuoteIndex + 1, endQuoteIndex)
        } catch (e: Exception) {
            showLog("getTypeState error: ${e.message}")
            return ""
        }
    }


}