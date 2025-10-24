package gh.datapost

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import gh.cark.NcZong
import gh.sj.MvS
import gh.sj.NcGoA
import kotlin.random.Random

object NcPointFun {
    private var isInsEventSuccess by MvS.bool()
    
    private val requestingSet = mutableSetOf<String>()
    
    fun postInsFun() {
        // 如果已经成功上传过，直接返回
        if (isInsEventSuccess) {
            NcZong.showLog("postInsFun already success, skip")
            return
        }
        
        val requestKey = "postInsFun"
        // 如果正在请求中，不再重复请求
        if (requestingSet.contains(requestKey)) {
            NcZong.showLog("postInsFun is requesting, skip")
            return
        }
        
        val data = NcZong.ifkJson.ifEmpty {
            val newData = NcPointPost.upInstallJson()
            NcZong.ifkJson = newData
            newData
        }
        
        // 标记为请求中
        requestingSet.add(requestKey)
        
        // postInsFun是必传事件，最多重试20次
        retryRequest(
            requestKey = requestKey,
            data = data,
            maxRetries = 20,
            eventName = "postInsFun"
        ) { success ->
            if (success) {
                // 上传成功，记录状态，后续不再上传
                isInsEventSuccess = true
                NcZong.ifkJson = ""
            }
        }
    }

    fun postAdFun(adJson: String) {
        val requestKey = "postAdFun_${System.currentTimeMillis()}"
        val data = NcPointPost.upAdJson(adJson)
        // postAdFun是必传事件，最多重试20次
        retryRequest(
            requestKey = requestKey,
            data = data,
            maxRetries = 20,
            eventName = "postAdFun"
        )
    }

    fun postPointFun(
        canRetry: Boolean,
        name: String,
        key1: String? = null,
        keyValue1: Any? = null,
        key2: String? = null,
        keyValue2: Any? = null
    ) {

        if (!canRetry && NcZong.akv.isNotBlank() && NcZong.getKeyTypeValue(NcZong.akv,"must_name_event") !="can") {
            return
        }
        
        val requestKey = "postPointFun_${name}_${System.currentTimeMillis()}"
        
        // 准备请求数据
        val data = if (key1 != null) {
            NcPointPost.upPointJson(name, key1, keyValue1, key2, keyValue2)
        } else {
            NcPointPost.upPointJson(name)
        }
        // canRetry为true是必传事件，重试20次；否则是非必传事件，重试2-5次
        val maxRetries = if (canRetry) 20 else Random.nextInt(2, 6)
        
        retryRequest(
            requestKey = requestKey,
            data = data,
            maxRetries = maxRetries,
            eventName = "postPointFun-${name}"
        )
    }
    

    private fun retryRequest(
        requestKey: String,
        data: String,
        maxRetries: Int,
        eventName: String,
        onComplete: ((success: Boolean) -> Unit)? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            var currentRetry = 0
            var isSuccess = false
            
            while (currentRetry <= maxRetries && !isSuccess) {
                try {
                    // 使用suspendCoroutine来将回调转换为挂起函数
                    isSuccess = suspendRequest(data, eventName, currentRetry)
                    
                    if (isSuccess) {
                        NcZong.showLog("$eventName success after $currentRetry retries")
                        break
                    }
                    
                    // 如果失败且还有重试次数，等待随机时间后重试
                    if (currentRetry < maxRetries) {
                        // 重试间隔时间10-40秒随机
                        val delayTime = Random.nextLong(10000, 41000)
                        NcZong.showLog("$eventName will retry after ${delayTime}ms, current retry: ${currentRetry + 1}/$maxRetries")
                        delay(delayTime)
                        currentRetry++
                    } else {
                        break
                    }
                } catch (e: Exception) {
                    NcZong.showLog("$eventName error: ${e.message}")
                    if (currentRetry < maxRetries) {
                        val delayTime = Random.nextLong(10000, 41000)
                        delay(delayTime)
                        currentRetry++
                    } else {
                        break
                    }
                }
            }
            
            // 请求完成，从请求中集合移除
            requestingSet.remove(requestKey)
            
            // 执行完成回调
            onComplete?.invoke(isSuccess)
            
            if (!isSuccess) {
                NcZong.showLog("$eventName failed after $maxRetries retries")
            }
        }
    }
    
    /**
     * 挂起函数：执行单次请求
     */
    private suspend fun suspendRequest(
        data: String,
        eventName: String,
        retryCount: Int
    ): Boolean = suspendCancellableCoroutine { continuation ->
        NcGoA.postPutData(data, object : NcGoA.CallbackMy {
            override fun onSuccess(response: String) {
                NcZong.showLog("$eventName request success, retry: $retryCount, response: $response")
                if (continuation.isActive) {
                    continuation.resumeWith(Result.success(true))
                }
            }

            override fun onFailure(error: String) {
                NcZong.showLog("$eventName request failure, retry: $retryCount, error: $error")
                if (continuation.isActive) {
                    continuation.resumeWith(Result.success(false))
                }
            }
        })
    }
}