package gh.ref

import android.util.Log
import kotlinx.coroutines.*
import gh.cark.NcZong
import gh.sj.MvS
import gh.sj.NetCong
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject
import java.util.Calendar
import kotlin.random.Random


class AdminRequestManager(private val ioScope: CoroutineScope) {

    private var dailyRequestCount: Int by MvS.int(0, "admin_daily_request_count")
    private var lastRequestDate: String by MvS.string("", "admin_last_request_date")

    // 请求状态
    @Volatile
    private var isRequesting = false

    // 定时任务Job
    private var scheduledJob: Job? = null



    // 重试相关
    private var retryJob: Job? = null
    private var currentRetryCount = 0




    /**
     * 启动Admin数据请求流程
     */
    fun startAdminDataFlow() {
        NcZong.showLog("AdminRequestManager: startAdminDataFlow")

        when {
            // 情况1：有配置
            NcZong.akv.isNotEmpty() -> {
                val userType = NcZong.getTypeState(NcZong.akv)
                when {
                    // 情况1：启动时有a配置
                    userType == "one" -> {
                        // 启动时有A配置，立即执行canNextFun
                        NetCong.canNextFun(NcZong.akv)
                        scheduleDelayedRequest()
                        startUserSchedule()
                    }
                    // 情况2：启动时有b配置
                    else -> {
                        startUserSchedule()
                    }
                }
            }
            // 情况2：没有配置
            else -> {
                requestAdminData(isRetry = false)
            }
        }
    }

    /**
     * 延迟请求（有a配置时使用）
     * 延迟时间：随机1秒-10分钟
     */
    private fun scheduleDelayedRequest() {
        val delayMs = Random.nextLong(1000L, 10 * 60 * 1000L) // 1s - 10min

        scheduledJob?.cancel()
        scheduledJob = ioScope.launch {
            delay(delayMs)
            if (isActive) {
                requestAdminData(false)
            }
        }
    }


    /**
     * 启动用户定时任务
     * 每x分钟（前后随机5分钟）请求一次，每x秒（前后随机0-10秒）请求一次，失败/超时重试1次
     */
    private fun startUserSchedule() {
        scheduledJob?.cancel()
        scheduledJob = ioScope.launch {
            while (isActive) {
                val abConfigure = NcZong.getKeyTypeValue(NcZong.akv, "\"ab_configure\"")
                val minutesA = parseAUserInterval(abConfigure)

                val minutesB = parseBUserInterval(abConfigure)
                Log.e("TAG", "startUserSchedule: minutesA=$minutesA===minutesB${minutesB}----${NcZong.getTypeState(NcZong.akv)}")
                val isA = NcZong.getTypeState(NcZong.akv) == "one"
                val user = if (isA) "a" else "b"
                val userUn = if (isA) "分钟" else "秒"

                val seconds = if (isA) minutesA else minutesB

                val randomOffsetMsA = Random.nextLong(-5 * 60 * 1000L, 5 * 60 * 1000L)
                val delayMsA = seconds * 60 * 1000L + randomOffsetMsA
                val actualDelayMsA = maxOf(30 * 1000L, delayMsA) // 最少30秒


                // 前后随机0-10秒
                val randomOffsetMsB = Random.nextLong(0, 10 * 1000L)
                val delayMsB = seconds * 1000L + randomOffsetMsB
                val actualDelayMsB = maxOf(1000L, delayMsB) // 最少1秒
                val actualDelayMs = if (isA) actualDelayMsA else actualDelayMsB
                delay(actualDelayMs)

                if (isActive) {
                    requestAdminDataWithBUserRetry()
                }
            }
        }
    }

    /**
     * b用户请求，失败后重试1次
     */
    private suspend fun requestAdminDataWithBUserRetry() {
        var success = false
        var retries = 0
        if (isRequesting) {
            return
        }
        // 检查每日请求上限
        if (!checkDailyLimit()) {
            return
        }

        isRequesting = true
        updateDailyRequestCount()
        while (!success && retries <= 1) {
            val deferred = CompletableDeferred<Boolean>()

            NetCong.requestAdmin(object : NetCong.AdminCallback {
                override fun onSuccess(response: String) {
                    deferred.complete(true)
                    handleRequestSuccess(response)
                }

                override fun onFailure(error: String) {
                    deferred.complete(false)
                    handleRequestFailure(true)
                }
            })

            success = deferred.await()

            if (!success && retries < 1) {
                retries++
                delay(30 * 1000L) // 重试间隔30秒
            }
        }
    }

    /**
     * 执行Admin数据请求
     * @param isRetry 是否是重试请求
     */
    private fun requestAdminData(isRetry: Boolean) {
        // 检查是否正在请求
        if (isRequesting) {
            return
        }

        // 检查每日请求上限
        if (!checkDailyLimit()) {
            return
        }

        isRequesting = true
        updateDailyRequestCount()


        // 设置60秒超时
        val timeoutJob = ioScope.launch {
            delay(60 * 1000L)
            if (isRequesting) {
                isRequesting = false
                handleRequestFailure(isRetry)
            }
        }

        NetCong.requestAdmin(object : NetCong.AdminCallback {
            override fun onSuccess(response: String) {
                timeoutJob.cancel()
                isRequesting = false
                handleRequestSuccess(response)
            }

            override fun onFailure(error: String) {
                timeoutJob.cancel()
                isRequesting = false
                handleRequestFailure(isRetry)
            }
        })
    }

    /**
     * 处理请求成功
     */
    private fun handleRequestSuccess(config: String) {

        // 取消重试Job
        retryJob?.cancel()
        retryJob = null
        currentRetryCount = 0

        // 处理配置
        processConfig(config)
        try {
            val ss = JSONObject(NcZong.akv)
            val num = ss.optString("a_p_n")
            NcZong.dailyRequestLimit = num.toInt()
        } catch (e: Exception) {
        }
    }

    /**
     * 处理请求失败
     */
    private fun handleRequestFailure(wasRetry: Boolean) {
        if (!wasRetry) {
            startRetryFlow()
        }
    }

    /**
     * 开始重试流程
     * 重试3-5次，总时长1-5分钟，间隔不少于30秒
     */
    private fun startRetryFlow() {
        retryJob?.cancel()
        currentRetryCount = 0

        // 随机选择重试次数（3-5次）
        val maxRetries = Random.nextInt(3, 6)

        // 随机选择总时长（1-5分钟）
        val totalDurationMs = Random.nextLong(60 * 1000L, 5 * 60 * 1000L)


        retryJob = ioScope.launch {
            val startTime = System.currentTimeMillis()

            while (currentRetryCount < maxRetries && isActive) {
                // 计算剩余时间和剩余次数
                val elapsedTime = System.currentTimeMillis() - startTime
                val remainingTime = totalDurationMs - elapsedTime
                val remainingRetries = maxRetries - currentRetryCount

                // 如果超过总时长，结束重试
                if (remainingTime <= 0) {
                    break
                }

                // 计算下次重试的延迟时间
                var nextDelay = if (remainingRetries > 1) {
                    remainingTime / remainingRetries
                } else {
                    remainingTime
                }

                // 确保间隔不少于30秒
                nextDelay = maxOf(30 * 1000L, nextDelay)

                delay(nextDelay)

                if (isActive) {
                    currentRetryCount++
                    requestAdminData(isRetry = true)

                    // 等待请求完成（最多60秒）
                    var waitTime = 0L
                    while (isRequesting && waitTime < 60000L && isActive) {
                        delay(500)
                        waitTime += 500
                    }

                    // 如果获取到配置，中断重试
                    if (NcZong.akv.isNotEmpty()) {
                        break
                    }
                }
            }

            if (NcZong.akv.isEmpty()) {
            }
        }
    }

    /**
     * 处理配置
     */
    private fun processConfig(newConfig: String) {
        val oldConfig = NcZong.akv
        val newUserType = NcZong.getTypeState(newConfig)
        val oldUserType = if (oldConfig.isNotEmpty()) NcZong.getTypeState(oldConfig) else ""


        when {
            // 获得配置但没有值
            newConfig.isEmpty() -> {
                if (oldConfig.isEmpty()) {
                    handleRequestFailure(false)
                } else {
                    // 继续使用旧配置的定时任务
                }
                startUserSchedule()
            }
            // 获得a配置
            newUserType == "one" -> {
                if (!NetCong.isCanNextGo) {
                    NcZong.akv = newConfig
                    NetCong.canNextFun(newConfig)
                } else {
                    NcZong.akv = newConfig
                }
                startUserSchedule()
            }
            // 获得b配置
            else -> {
                when {
                    // 之前是a配置，放弃新的b配置
                    oldUserType == "one" -> {
                        // 继续使用旧的a配置
                    }
                    // 之前没有配置，获得B配置，触发重试（期望获得A配置）
                    oldConfig.isEmpty() -> {
                        // 不保存B配置，触发B流程
                        NcZong.akv = newConfig
                        startUserSchedule()
                    }
                    // 之前已有b配置，使用新的b配置
                    else -> {
                        NcZong.akv = newConfig
                        startUserSchedule()
                    }
                }
            }
        }
    }

    /**
     * 检查每日请求上限
     */
    private fun checkDailyLimit(): Boolean {
        val today = getCurrentDate()

        // 如果是新的一天，重置计数
        if (lastRequestDate != today) {
            dailyRequestCount = 0
            lastRequestDate = today
        }
        return dailyRequestCount < NcZong.dailyRequestLimit
    }

    /**
     * 更新每日请求计数
     */
    private fun updateDailyRequestCount() {
        val today = getCurrentDate()

        if (lastRequestDate != today) {
            dailyRequestCount = 0
            lastRequestDate = today
        }
        dailyRequestCount++
    }

    /**
     * 获取当前日期字符串（用于判断自然日）
     */
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${
            calendar.get(
                Calendar.DAY_OF_MONTH
            )
        }"
    }

    /**
     * 解析a用户的请求间隔（分钟）
     */
    private fun parseAUserInterval(abConfigure: String): Long {
        return try {
            val parts = abConfigure.split("-")
            if (parts.size >= 1) {
                parts[0].toLongOrNull() ?: 60L
            } else {
                60L
            }
        } catch (e: Exception) {
            60L
        }
    }

    /**
     * 解析b用户的请求间隔（秒）
     */
    private fun parseBUserInterval(abConfigure: String): Long {
        return try {
            val parts = abConfigure.split("-")
            if (parts.size >= 2) {
                parts[1].toLongOrNull() ?: 60L
            } else {
                60L
            }
        } catch (e: Exception) {
            60L
        }
    }

    /**
     * 取消所有任务
     */
    fun cancelAll() {
        scheduledJob?.cancel()
        retryJob?.cancel()
        isRequesting = false
    }
}

