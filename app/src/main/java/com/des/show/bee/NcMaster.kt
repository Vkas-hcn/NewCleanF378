package com.des.show.bee

import android.Manifest
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.des.show.bee.cf.NcChong
import com.des.show.bee.qlwj.presentation.NcTrans
import com.des.show.bee.databinding.NcMasBinding
import com.des.show.bee.yy.NcAppMc
import java.text.DecimalFormat
import kotlin.math.max

// 使用枚举类替代常量定义
enum class PermissionConstants {
    STORAGE_PERMISSION_CODE,
    RESULT_PERMISSION_CODE,
    PREF_NAME,
    KEY_PERMISSION_DENIED_COUNT
}

// 使用枚举类表示跳转类型
enum class JumpDestination(val type: Int) {
    TRANS(0),
    APP(1),
    WEN(2),
    CHONG(3)
}

// 扩展函数用于设置窗口边距
fun View.applyWindowInsets(insets: WindowInsetsCompat) {
    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
    this.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
}

class NcMaster : AppCompatActivity() {
    // 使用不同的初始化方式
    private lateinit var binding: NcMasBinding
    
    // 伴生对象使用不同的实现
    companion object {
        var jumpType = -1
        
        private fun getPermissionCode(constant: PermissionConstants): Int {
            return when (constant) {
                PermissionConstants.STORAGE_PERMISSION_CODE -> 10000
                PermissionConstants.RESULT_PERMISSION_CODE -> 10001
                else -> throw IllegalArgumentException("Invalid permission code constant")
            }
        }
        
        private fun getPrefKey(constant: PermissionConstants): String {
            return when (constant) {
                PermissionConstants.PREF_NAME -> "permission_prefs"
                PermissionConstants.KEY_PERMISSION_DENIED_COUNT -> "permission_denied_count"
                else -> throw IllegalArgumentException("Invalid preference key constant")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 重新组织初始化流程
        binding = NcMasBinding.inflate(layoutInflater)
        
        setupActivity()
        initializeUI()
        setupListeners()
        updateStorageInfo()
    }

    // 提取UI设置为单独方法
    private fun setupActivity() {
        enableEdgeToEdge()
        setContentView(binding.root)
        
        // 应用窗口边距
        findViewById<View>(R.id.main)?.apply {
            ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
                view.applyWindowInsets(insets)
                insets
            }
        }
        
        // 隐藏ActionBar
        supportActionBar?.hide()
    }
    
    // 初始化UI状态
    private fun initializeUI() {
        // 可以在这里添加UI初始化代码
    }
    
    // 重新组织监听器设置
    private fun setupListeners() {
        // 使用run函数简化绑定
        binding.run {
            materialButton.setOnClickListener {
                handleCleanButtonClick()
            }

            tvApp.setOnClickListener {
                jumpType = JumpDestination.APP.type
                startScanActivity()
            }

            tvLarge.setOnClickListener {
                jumpType = JumpDestination.WEN.type
                checkPermissionsAndScan()
            }
            
            tvDuplicate.setOnClickListener {
                jumpType = JumpDestination.CHONG.type
                checkPermissionsAndScan()
            }

            imgSet.setOnClickListener {
                startActivity(Intent(this@NcMaster, NcNet::class.java))
            }

            missIn.missPm.setOnClickListener { }

            missIn.tvCancel.setOnClickListener {
                missIn.missPm.isVisible = false
                handlePermissionDenied()
            }

            missIn.tvYes.setOnClickListener {
                missIn.missPm.isVisible = false
                requestStoragePermission()
            }
        }
    }

    // 更清晰的按钮处理方法
    private fun handleCleanButtonClick() {
        jumpType = JumpDestination.TRANS.type
        checkPermissionsAndScan()
    }
    
    // 更新存储信息并直接设置UI组件
    private fun updateStorageInfo() {
        try {
            val internalStat = StatFs(Environment.getDataDirectory().path)

            val blockSize = internalStat.blockSizeLong
            val totalBlocks = internalStat.blockCountLong
            val availableBlocks = internalStat.availableBlocksLong

            val totalUserBytes = totalBlocks * blockSize  // 用户可见的总空间
            val availableBytes = availableBlocks * blockSize  // 用户可用空间
            val actualTotalBytes = getTotalDeviceStorageAccurate()
            val displayTotalBytes = max(actualTotalBytes, totalUserBytes)
            val displayFreeBytes = availableBytes
            val displayUsedBytes = displayTotalBytes - displayFreeBytes

            val usedStorageFormatted = formatStorageSize(displayUsedBytes)
            val totalStorageFormatted = formatStorageSize(displayTotalBytes)

            binding.tvUser.text = usedStorageFormatted.first
            binding.tvTo.text = "/${totalStorageFormatted.first}"




        } catch (e: Exception) {
            e.printStackTrace()
            binding.tvUser.text = "-- GB"
            binding.tvTo.text = "/-- GB"
        }
    }

    private fun getTotalDeviceStorageAccurate(): Long {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val storageStatsManager =
                    getSystemService(STORAGE_STATS_SERVICE) as StorageStatsManager
                return storageStatsManager.getTotalBytes(StorageManager.UUID_DEFAULT)
            }

            val internalStat = StatFs(Environment.getDataDirectory().path)

            val internalTotal = internalStat.blockCountLong * internalStat.blockSizeLong

            val storagePaths = arrayOf(
                Environment.getRootDirectory().absolutePath,      // /system
                Environment.getDataDirectory().absolutePath,      // /data
                Environment.getDownloadCacheDirectory().absolutePath // /cache
            )

            var total: Long = 0
            for (path in storagePaths) {
                val stat = StatFs(path)
                val blockSize = stat.blockSizeLong
                val blockCount = stat.blockCountLong
                total += blockSize * blockCount
            }

            val withSystemOverhead = total + (total * 0.07).toLong()

            max(internalTotal, withSystemOverhead)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val internalStat = StatFs(Environment.getDataDirectory().path)
                val internalTotal = internalStat.blockCountLong * internalStat.blockSizeLong
                internalTotal + (internalTotal * 0.12).toLong()
            } catch (innerException: Exception) {
                innerException.printStackTrace()
                0L
            }
        }
    }

    private fun formatStorageSize(bytes: Long): Pair<String, String> {
        return when {
            bytes >= 1000L * 1000L * 1000L -> {
                val gb = bytes.toDouble() / (1000L * 1000L * 1000L)
                val formatted = if (gb >= 10.0) {
                    DecimalFormat("#").format(gb)
                } else {
                    DecimalFormat("#.#").format(gb)
                }
                Pair("$formatted GB", "GB")
            }

            bytes >= 1000L * 1000L -> {
                val mb = bytes.toDouble() / (1000L * 1000L)
                val formatted = DecimalFormat("#").format(mb)
                Pair("$formatted MB", "MB")
            }

            bytes >= 1000L -> {
                val kb = bytes.toDouble() / 1000L
                val formatted = DecimalFormat("#").format(kb)
                Pair("$formatted KB", "KB")
            }

            else -> {
                Pair("$bytes B", "B")
            }
        }
    }

    // 使用密封类处理权限状态
    private sealed class PermissionState {
        object Granted : PermissionState()
        object Denied : PermissionState()
        object RationaleRequired : PermissionState()
    }
    
    private fun checkPermissionsAndScan() {
        if (!checkStoragePermission()) {
            binding.missIn.missPm.isVisible = true
        } else {
            startScanActivity()
        }
    }
    
    // 重命名方法以提高可读性
    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> {
                requestManageExternalStoragePermission()
            }
            else -> {
                requestLegacyStoragePermission()
            }
        }
    }

    private fun requestManageExternalStoragePermission() {
        try {
            // 使用apply函数配置Intent
            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
                startActivityForResult(this, getPermissionCode(PermissionConstants.RESULT_PERMISSION_CODE))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                startActivityForResult(
                    Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION),
                    getPermissionCode(PermissionConstants.RESULT_PERMISSION_CODE)
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
                navigateToAppSettings()
            }
        }
    }

    private fun requestLegacyStoragePermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(
            this, 
            permissions, 
            getPermissionCode(PermissionConstants.STORAGE_PERMISSION_CODE)
        )
    }

    private fun handlePermissionDenied() {
        val permissionManager = PermissionManager(this)
        val deniedCount = permissionManager.getDeniedCount()
        permissionManager.incrementDeniedCount()

        when (deniedCount) {
            0 -> showSimplePermissionDeniedDialog()
            else -> showDetailedPermissionDeniedDialog()
        }
    }
    
    // 将权限管理逻辑封装成内部类
    private inner class PermissionManager(private val context: Context) {
        private val prefs by lazy { 
            context.getSharedPreferences(
                getPrefKey(PermissionConstants.PREF_NAME), 
                Context.MODE_PRIVATE
            ) 
        }
        
        fun getDeniedCount(): Int {
            return prefs.getInt(
                getPrefKey(PermissionConstants.KEY_PERMISSION_DENIED_COUNT), 
                0
            )
        }
        
        fun incrementDeniedCount() {
            val currentCount = getDeniedCount()
            prefs.edit()
                .putInt(getPrefKey(PermissionConstants.KEY_PERMISSION_DENIED_COUNT), currentCount + 1)
                .apply()
        }
    }

    private fun showSimplePermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Requires storage permissions")
            .setMessage("To clean up your device, the app needs access to storage.")
            .setPositiveButton("Re-authorization") { _, _ ->
                requestStoragePermission()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDetailedPermissionDeniedDialog() {
        val message = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            "The application requires\"Manage All Files\" permission to clean up your device. Please find this app in the settings and enable the\"Allow management of all files\"permission."
        } else {
            "The app requires storage permission to clean up your device. Please find the app in settings and enable the \"Storage\" permission."
        }

        AlertDialog.Builder(this)
            .setTitle("Requires storage permissions")
            .setMessage(message)
            .setPositiveButton("Go to Settings") { _, _ ->
                navigateToAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(false)
            .show()
    }

    private fun navigateToAppSettings() {
        try {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                startActivity(this)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    // 使用策略模式处理跳转
    private fun startScanActivity() {
        val intentFactory = when(jumpType) {
            JumpDestination.TRANS.type -> { { Intent(this, NcTrans::class.java) } }
            JumpDestination.APP.type -> { { Intent(this, NcAppMc::class.java) } }
            JumpDestination.WEN.type -> { { Intent(this, NcWen::class.java) } }
            JumpDestination.CHONG.type -> { { Intent(this, NcChong::class.java) } }
            else -> { { null } }
        }
        
        intentFactory()?.let { startActivity(it) }
    }

    override fun onResume() {
        super.onResume()
        if (checkStoragePermission()) {
            binding.missIn.missPm.isVisible = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == getPermissionCode(PermissionConstants.RESULT_PERMISSION_CODE)) {
            if (checkStoragePermission()) {
                startScanActivity()
            } else {
                binding.missIn.missPm.isVisible = true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == getPermissionCode(PermissionConstants.STORAGE_PERMISSION_CODE)) {
            val permissionState = analyzePermissionResult(permissions, grantResults)
            
            when (permissionState) {
                PermissionState.Granted -> startScanActivity()
                PermissionState.RationaleRequired -> binding.missIn.missPm.isVisible = true
                PermissionState.Denied -> showDetailedPermissionDeniedDialog()
            }
        }
    }
    
    // 提取权限结果分析逻辑
    private fun analyzePermissionResult(
        permissions: Array<out String>, 
        grantResults: IntArray
    ): PermissionState {
        val allGranted = grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        
        if (allGranted) {
            return PermissionState.Granted
        }
        
        val shouldShowRationale = permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(this, it)
        }
        
        return if (shouldShowRationale) {
            PermissionState.RationaleRequired
        } else {
            PermissionState.Denied
        }
    }
}