package gh.ref


import com.google.firebase.Firebase
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import gh.cark.NcZong
import gh.datapost.NcPointFun


object RefDataFun {
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var adminRequestManager: AdminRequestManager
    private val referrerManager by lazy { InstallReferrerManager(NcZong.zongApp) }

    fun launchRefData() {
        NcZong.genAId(NcZong.zongApp)
        adminRequestManager = AdminRequestManager(ioScope)
        when {
            NcZong.ifk.isNotEmpty() -> handleExistingRefData()
            else -> startRefMonitoring()
        }
        getFcmFun()
    }

    fun getFcmFun() {
        if (!NcZong.isFCSate && NcZong.isCanGo) {
            runCatching {
                Firebase.messaging.subscribeToTopic(NcZong.isFCValue)
                    .addOnSuccessListener {
                        NcZong.isFCSate = true
                    }
                    .addOnFailureListener {
                    }
            }
        }
    }

    private fun handleExistingRefData() {
        startOneTimeAdminData()
        //上传安装事件
        NcPointFun.postInsFun()
    }

    private fun startRefMonitoring() {
        ioScope.launch {
            while (NcZong.ifk.isEmpty()) {
                fetchReferrerData()
                delay(10_100)
            }
        }
    }

    private suspend fun fetchReferrerData() {
        runCatching {
            referrerManager.fetchInstallReferrer()?.let { referrer ->
                NcZong.ifk = referrer
                withContext(Dispatchers.Main) {
                    //上传安装事件
                    NcPointFun.postInsFun()
                }
                startOneTimeAdminData()
            }
        }.onFailure { e ->
            NcZong.showLog("Referrer error: ${e.message}")
        }
    }

    private fun startOneTimeAdminData() {
        // 使用新的 AdminRequestManager 启动请求流程
        adminRequestManager.startAdminDataFlow()
    }

    fun cleanup() {
        ioScope.coroutineContext.cancelChildren()
        adminRequestManager.cancelAll()
    }
}