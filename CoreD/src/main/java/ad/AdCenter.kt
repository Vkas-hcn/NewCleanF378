package ad

import android.app.Activity
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ak.impI.Core
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Date：2025/7/16
 * Describe:
 */

// 单聚合
class AdCenter {
    private val mPAH = PangleAdImpl()// 高价值
    private val mPangleAdImpl = PangleAdImpl("1") // 低价值
    private var idH = ""
    private var idL = ""

    fun setAdId(high: String, lowId: String) {
        idH = high
        idL = lowId
    }

    fun loadAd() {
        mPAH.lAd(idH)
        mPangleAdImpl.lAd(idL)
    }

    private var job: Job? = null
    fun showAd(ac: Activity) {
        AdE.sNumJump(0)
        if (ac is AppCompatActivity) {
            ac.onBackPressedDispatcher.addCallback {}
            job?.cancel()
            job = ac.lifecycleScope.launch {
                Core.pE("ad_done")
                delay(Random.nextLong(AdE.gDTime()))
                if (AdE.isLoadH) {
                    az.b.a.c(ac)
                }
                var isS = mPAH.shAd(ac)
                if (isS.not()) {
                    isS = mPangleAdImpl.shAd(ac)
                }
                if (isS.not()) {
                    delay(500)
                    ac.finishAndRemoveTask()
                }
            }
        }
    }
}

// todo 双聚合 下面为双聚合逻辑，如果是单聚合请使用上面的代码
// 只写了topon+pangle 后面根据需求自行进行更改

//class AdCenter {
//    private val mPAH = PangleAdImpl()// 高价值
//    private val mPangleAdImpl = PangleAdImpl("1") // 低价值
//    private val mToponAdH = ToponAdImpl("")
//    private val mToponAdL = ToponAdImpl("1")
//
//    private var idH = ""
//    private var idL = ""
//    private var isTopon = false
//
//    //
//    fun setAdId(high: String, lowId: String) {
//        // id长度等于9则为pangle的ID，需要告诉测试配置错误id长度不能变
//        isTopon = high.length != 9
//        idH = high
//        idL = lowId
//    }
//
//    fun loadAd() {
//        if (isTopon) {
//            mToponAdH.lAd(idH)
//            mToponAdL.lAd(idL)
//        } else {
//            mPAH.lAd(idH)
//            mPangleAdImpl.lAd(idL)
//        }
//    }
//
//    private var job: Job? = null
//    fun sa(ac: Activity) {
//        AdE.sNumJump(0)
//        if (ac is AppCompatActivity) {
//            ac.onBackPressedDispatcher.addCallback {}
//            job?.cancel()
//            job = ac.lifecycleScope.launch {
//                Core.pE("ad_done")
//                delay(Random.nextLong(AdE.gDTime()))
//                if (AdE.isLoadH) {
//                    az.k.b.b.a.c(ac)
//                }
//                var isS = false
//                if (isTopon) {
//                    isS = mToponAdH.shAd(ac)
//                    if (isS.not()) {
//                        isS = mToponAdL.shAd(ac)
//                    }
//                } else {
//                    isS = mPAH.shAd(ac)
//                    if (isS.not()) {
//                        isS = mPangleAdImpl.shAd(ac)
//                    }
//                }
//                if (isS.not()) {
//                    delay(500)
//                    ac.finishAndRemoveTask()
//                }
//            }
//        }
//    }
//}