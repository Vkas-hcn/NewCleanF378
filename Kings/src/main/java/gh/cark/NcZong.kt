package gh.cark

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.appsflyer.AFAdRevenueData
import com.appsflyer.AdRevenueScheme
import com.appsflyer.AppsFlyerLib
import com.appsflyer.MediationNetwork
import gh.cark.init.core.InitTaskScheduler
import gh.sj.MvS
import gh.i.DAL
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

object NcZong {
    lateinit var dal: DAL
    lateinit var zongApp: Application

    //测试连接删除
    var isCanGo = true

    //admin配置缓存的key
    var akv by MvS.string()

    // referrer 配置缓存的key
    var ifk by MvS.string()

    var ifkJson by MvS.string()

    // android id 配置缓存的key
    var aau by MvS.string()


    var isFCSate: Boolean by MvS.bool()

    var r_c_t_s by MvS.string()
    var r_c_t_s_s by MvS.string()
    var isFCValue = "twogjasd"
    var dailyRequestLimit: Int = 1000
    
    /**
     * 初始化入口 - 使用任务调度器分散执行逻辑
     */
    fun initSp(app: Application) {
        // 保存应用实例和配置
        zongApp = app
        //TODO 环境切换
        isCanGo = false

        // 通过任务调度器执行初始化（隐藏执行顺序）
        InitTaskScheduler.scheduleAndExecute(app)
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
            aau = finalId
        }
    }

    fun showLog(msg: String) {
        if (isCanGo) {
            return
        }
        Log.e("Nc", msg)
    }

    fun getUpUrl(): String {
        return "https://test-language.dashgocleanday.com/suit/showroom"
    }

    fun getAdminUrl(): String {
        return "https://azzs.keepdevicesfreshppowerclean.com/apitest/juyyu/"

    }

    fun getPangKey(): String {
        return "8580262"

    }

    fun getToPonAppId(): String {
        return "h670e13c4e3ab6"

    }

    fun getToPonAppKey(): String {
        return "ac360a993a659579a11f6df50b9e78639"

    }

    fun getApplyKey(): String {
        return "5MiZBZBjzzChyhaowfLpyR"
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

    fun getKeyTypeValue(jsonStr: String?, key: String): String {
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