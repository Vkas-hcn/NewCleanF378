package com.desolation.spreads.reach

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.desolation.spreads.reach.databinding.NcMasBinding


import android.Manifest
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.StatFs
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.Settings
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.desolation.spreads.reach.databinding.NcNameBinding
import com.desolation.spreads.reach.yy.NcAppMc
import java.text.DecimalFormat
import kotlin.math.max

class NcMaster : AppCompatActivity() {
    private val binding by lazy { NcMasBinding.inflate(layoutInflater) }

    companion object {
        private const val STORAGE_PERMISSION_CODE = 10000
        private const val RESULT_PERMISSION_CODE = 10001
        private const val PREF_NAME = "permission_prefs"
        private const val KEY_PERMISSION_DENIED_COUNT = "permission_denied_count"
        var jumpType = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        this.supportActionBar?.hide()

        updateStorageInfo()

        binding.materialButton.setOnClickListener {
           jumpType = 0
            checkPermissionsAndScan()
        }

        binding.tvApp.setOnClickListener {
            jumpType = 1
            startScanActivity()
        }

        binding.tvLarge.setOnClickListener {
            jumpType = 2
            checkPermissionsAndScan()
        }
        binding.tvDuplicate.setOnClickListener {
            jumpType = 3
            checkPermissionsAndScan()
        }

        binding.imgSet.setOnClickListener {
            startActivity(Intent(this, NcNet::class.java))
        }

        binding.missIn.missPm.setOnClickListener {
        }

        binding.missIn.tvCancel.setOnClickListener {
            binding.missIn.missPm.isVisible = false
            handlePermissionDenied()
        }

        binding.missIn.tvYes.setOnClickListener {
            binding.missIn.missPm.isVisible = false
            requestStoragePermission()
        }
    }

    override fun onResume() {
        super.onResume()
        if (hasStoragePermission()) {
            binding.missIn.missPm.isVisible = false
        }
    }

    private fun updateStorageInfo() {
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



            val totalUserBytesFormatted = formatStorageSize(totalUserBytes)
            val usedStorageFormatted = formatStorageSize(displayUsedBytes)

            binding.tvUser.text = usedStorageFormatted.first
            binding.tvTo.text = "/${totalUserBytesFormatted.first}"

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

    private fun checkPermissionsAndScan() {
        if (!hasStoragePermission()) {
            binding.missIn.missPm.isVisible = true
        } else {
            startScanActivity()
        }
    }

    private fun hasStoragePermission(): Boolean {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestManageExternalStoragePermission()
        } else {
            requestTraditionalStoragePermission()
        }
    }

    private fun requestManageExternalStoragePermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivityForResult(intent, RESULT_PERMISSION_CODE)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, RESULT_PERMISSION_CODE)
            } catch (ex: Exception) {
                ex.printStackTrace()
                openAppSettings()
            }
        }
    }

    private fun requestTraditionalStoragePermission() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        ActivityCompat.requestPermissions(this, permissions, STORAGE_PERMISSION_CODE)
    }

    private fun handlePermissionDenied() {
        val deniedCount = getPermissionDeniedCount()
        incrementPermissionDeniedCount()

        when {
            deniedCount == 0 -> {
                showSimplePermissionDeniedDialog()
            }
            deniedCount >= 1 -> {
                showDetailedPermissionDeniedDialog()
            }
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
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .setCancelable(false)
            .show()
    }

    private fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                startActivity(Intent(Settings.ACTION_SETTINGS))
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun startScanActivity() {
        when(jumpType){
            0 -> {
                val intent = Intent(this, NcTrans::class.java)
                startActivity(intent)
            }
            1 -> {
                val intent = Intent(this, NcAppMc::class.java)
                startActivity(intent)
            }
            2 -> {
                val intent = Intent(this, NcWen::class.java)
                startActivity(intent)
            }
            3 -> {
                val intent = Intent(this, NcChong::class.java)
                startActivity(intent)
            }
        }
    }

    private fun getPermissionDeniedCount(): Int {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(KEY_PERMISSION_DENIED_COUNT, 0)
    }

    private fun incrementPermissionDeniedCount() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val currentCount = prefs.getInt(KEY_PERMISSION_DENIED_COUNT, 0)
        prefs.edit().putInt(KEY_PERMISSION_DENIED_COUNT, currentCount + 1).apply()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_PERMISSION_CODE) {
            if (hasStoragePermission()) {
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
        if (requestCode == STORAGE_PERMISSION_CODE) {
            val allGranted = grantResults.isNotEmpty() &&
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }

            if (allGranted) {
                startScanActivity()
            } else {
                val shouldShowRationale = permissions.any { permission ->
                    ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }

                if (!shouldShowRationale) {
                    showDetailedPermissionDeniedDialog()
                } else {
                    binding.missIn.missPm.isVisible = true
                }
            }
        }
    }
}