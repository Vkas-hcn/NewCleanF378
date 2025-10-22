package zj.go.ref

import android.content.Context
import android.util.Log
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener

import kotlinx.coroutines.coroutineScope

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// 安装来源管理类
class InstallReferrerManager(private val context: Context) {
    private var currentClient: InstallReferrerClient? = null

    suspend fun fetchInstallReferrer(): String? = coroutineScope {
        suspendCancellableCoroutine { continuation ->
            try {
                val client = InstallReferrerClient.newBuilder(context).build()
                currentClient = client

                client.startConnection(object : InstallReferrerStateListener {
                    override fun onInstallReferrerSetupFinished(responseCode: Int) {
                        when (responseCode) {
                            InstallReferrerClient.InstallReferrerResponse.OK -> {
                                val referrer = client.installReferrer.installReferrer
                                continuation.resume(referrer.takeIf { it.isNotEmpty() })
                            }

                            else -> continuation.resume(null)
                        }
                        safeEndConnection()
                    }

                    override fun onInstallReferrerServiceDisconnected() {
                        safeEndConnection()
                        if (!continuation.isCompleted) {
                            continuation.resume(null)
                        }
                    }
                })
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }

    private fun safeEndConnection() {
        runCatching { currentClient?.endConnection() }
            .onFailure { Log.w("Referrer", "End connection failed: ${it.message}") }
        currentClient = null
    }
}



