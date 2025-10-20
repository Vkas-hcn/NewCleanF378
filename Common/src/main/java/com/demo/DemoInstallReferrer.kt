package com.demo

import android.content.Context
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.android.installreferrer.api.ReferrerDetails
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Date：2025/10/19
 * Describe:
 * todo
 * 该类为demo类，方便代码熟悉，不能直接复制，
 * 需要对这个类的具体实现做差异化
 */
class DemoInstallReferrer {
    // mReferrer 需要做持久化缓存，获取到后就不需要在获取了
    private var mReferrer = ""
    private val mIoScope = CoroutineScope(Dispatchers.IO)
    fun fe(context: Context) {
        if (mReferrer.isBlank()) {
            mIoScope.launch {
                while (mReferrer.isBlank()) {
                    fetchReferrer(context)
                    delay(18000)
                }
            }
        } else {
            // 检查 install 事件是否上报成功，没成功需要上报TBA

        }
    }

    private fun fetchReferrer(context: Context) {
        val referrerClient = InstallReferrerClient.newBuilder(context).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(p0: Int) {
                runCatching {
                    if (p0 == InstallReferrerClient.InstallReferrerResponse.OK) {
                        val response: ReferrerDetails = referrerClient.installReferrer
                        mReferrer = response.installReferrer
                        //todo 需要上报referrer，且上报成功后就不再上报，没成功需要多尝试，
                        // 如果App都死了还没有成功下一次冷启动需要重试
                        referrerClient.endConnection()
                    } else {
                        referrerClient.endConnection()
                    }
                }.onFailure {
                    referrerClient.endConnection()
                }
            }

            override fun onInstallReferrerServiceDisconnected() = Unit
        })
    }
}