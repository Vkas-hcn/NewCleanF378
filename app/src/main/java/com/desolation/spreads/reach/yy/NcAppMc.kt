package com.desolation.spreads.reach.yy

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.desolation.spreads.reach.R
import com.desolation.spreads.reach.databinding.NcAppBinding
import com.desolation.spreads.reach.yy.adapter.AppAdapter
import com.desolation.spreads.reach.yy.model.AppInfo

class NcAppMc : AppCompatActivity() {
    private val binding by lazy { NcAppBinding.inflate(layoutInflater) }
    private val appList = mutableListOf<AppInfo>()
    private lateinit var appAdapter: AppAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.app)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUI()
        loadAppList()
    }

    private fun setupUI() {
        // 设置返回按钮
        binding.appCompatTextView.setOnClickListener {
            finish()
        }

        // 设置RecyclerView
        binding.rvApp.layoutManager = LinearLayoutManager(this)
        appAdapter = AppAdapter(appList) { appInfo ->
            showUninstallDialog(appInfo)
        }
        binding.rvApp.adapter = appAdapter
    }

    private fun loadAppList() {
        val app = getInstalledApps()
        Log.e("TAG", "loadAppList: $app")
        appList.clear() // 清除旧数据
        appList.addAll(app)
        // 按安装时间排序（最新安装的在前面）
        appList.sortByDescending { it.installTime }

        // 更新适配器
        appAdapter.notifyDataSetChanged()
    }


    private fun getInstalledApps(): List<AppInfo> {
        val packageManager = packageManager
        val apps = mutableListOf<AppInfo>()
        // 获取当前应用的包名
        val currentPackageName = applicationContext.packageName

        try {
            val packages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

            for (packageInfo in packages) {
                // 过滤掉系统应用和当前应用
                if (packageInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0 &&
                    packageInfo.packageName != currentPackageName
                ) {
                    val appName = packageManager.getApplicationLabel(packageInfo).toString()
                    val appIcon = packageManager.getApplicationIcon(packageInfo)
                    val installTime =
                        packageManager.getPackageInfo(packageInfo.packageName, 0).firstInstallTime

                    apps.add(
                        AppInfo(
                            packageName = packageInfo.packageName,
                            appName = appName,
                            icon = appIcon,
                            installTime = installTime,
                            isSystemApp = false
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return apps.sortedBy { it.appName }
    }

    private fun showUninstallDialog(appInfo: AppInfo) {
        AlertDialog.Builder(this)
            .setTitle("Uninstall the app")
            .setMessage("Are you sure you want to uninstall ${appInfo.appName}?")
            .setPositiveButton("Yes") { _, _ ->
                uninstallApp(appInfo.packageName)
            }
            .setNegativeButton("Cancle", null)
            .show()
    }

    private fun uninstallApp(packageName: String) {
        val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE)
        intent.data = Uri.parse("package:$packageName")
        intent.putExtra(Intent.EXTRA_RETURN_RESULT, true)
        startActivityForResult(intent, UNINSTALL_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UNINSTALL_REQUEST_CODE) {
            // 刷新应用列表
            loadAppList()
        }
    }

    companion object {
        private const val UNINSTALL_REQUEST_CODE = 1001
    }
}