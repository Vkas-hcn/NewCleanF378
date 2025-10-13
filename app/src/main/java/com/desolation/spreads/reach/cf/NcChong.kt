package com.desolation.spreads.reach.cf

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.desolation.spreads.reach.NcEnd
import com.desolation.spreads.reach.R
import com.desolation.spreads.reach.cf.adapter.DuplicateCategoryAdapter
import com.desolation.spreads.reach.cf.model.DuplicateCategory
import com.desolation.spreads.reach.cf.model.DuplicateFile
import com.desolation.spreads.reach.cf.utils.FileUtils
import com.desolation.spreads.reach.databinding.NcChongBinding
import java.io.File

class NcChong : AppCompatActivity() {
    private val binding by lazy { NcChongBinding.inflate(layoutInflater) }
    private val handler = Handler(Looper.getMainLooper())
    private var categories: List<DuplicateCategory> = emptyList()
    private var adapter: DuplicateCategoryAdapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.file)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupViews()

        showLoadingScreen()
        startFileScan()

    }


    private fun setupViews() {
        // Setup RecyclerView
        binding.rvDuplicateFiles.layoutManager = LinearLayoutManager(this)
        adapter = DuplicateCategoryAdapter(
            onItemClickListener = { file ->
                updateDeleteButtonState()
            },
            onCategoryClickListener = { category ->
                // Handle category click if needed
            }
        )
        binding.rvDuplicateFiles.adapter = adapter

        // Setup back button
        binding.textBack.setOnClickListener {
            finish()
        }

        // Setup delete button
        binding.btnDelete.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun showLoadingScreen() {

        binding.inLoad.imgLogo.setImageResource(R.drawable.dup_icon)
        binding.inLoad.tvTip.text = "Scanning..."
        binding.inLoad.root.setOnClickListener { }
        binding.inLoad.imgBack.setOnClickListener { finish() }
        binding.inLoad.missPm.visibility = View.VISIBLE

        // Set rotation animation
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
            fillAfter = true
        }

        binding.inLoad.imgLoad.startAnimation(rotateAnimation)
    }

    private fun hideLoadingScreen() {
        binding.inLoad.imgLoad.clearAnimation()
        binding.inLoad.missPm.visibility = View.GONE
    }

    private fun startFileScan() {
        // Show loading for 1 second, then start scanning
        handler.postDelayed({
            Thread {
                try {
                    categories = FileUtils.scanDuplicateFiles(this)
                    handler.post {
                        hideLoadingScreen()
                        adapter?.updateCategories(categories)
                        updateDeleteButtonState()
                    }
                } catch (e: Exception) {
                    handler.post {
                        hideLoadingScreen()
                        Toast.makeText(this, "Error scanning files", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }, 1000)
    }

    private fun updateDeleteButtonState() {
        val selectedFiles = getSelectedFiles()
        val hasSelected = selectedFiles.isNotEmpty()
        binding.btnDelete.isEnabled = hasSelected

        // 更新删除按钮文本，显示选中文件的总大小
        if (hasSelected) {
            val totalSize = selectedFiles.sumOf { it.size }
            val formattedSize = formatFileSize(totalSize)
            binding.btnDelete.text = "Delete ($formattedSize)"
        } else {
            binding.btnDelete.text = "Delete"
        }
    }

    /**
     * 将字节大小格式化为易读的格式（如KB, MB, GB）
     */
    private fun formatFileSize(bytes: Long): String {
        if (bytes < 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        var size = bytes.toDouble()
        var unitIndex = 0

        // 将字节转换为合适的单位
        while (size >= 1024 && unitIndex < units.size - 1) {
            size /= 1024
            unitIndex++
        }

        // 格式化结果，根据大小决定保留小数位数
        return if (unitIndex >= 1) {
            // 对于大于或等于KB的单位，保留一位小数
            String.format("%.1f%s", size, units[unitIndex])
        } else {
            // 对于字节单位，不保留小数
            String.format("%.0f%s", size, units[unitIndex])
        }
    }

    private fun showDeleteConfirmationDialog() {
        val selectedFiles = getSelectedFiles()
        if (selectedFiles.isEmpty()) {
            Toast.makeText(this, "Please select files to delete", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Delete Files")
            .setMessage("Are you sure you want to delete ${selectedFiles.size} selected files?")
            .setPositiveButton("Delete") { _, _ ->
                performDelete(selectedFiles)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun getSelectedFiles(): List<DuplicateFile> {
        return categories.flatMap { category ->
            category.files.filter { it.isSelected }
        }
    }

    private fun performDelete(selectedFiles: List<DuplicateFile>) {
        Thread {
            var deletedSize = 0L
            for (file in selectedFiles) {
                try {
                    val f = File(file.path)
                    if (f.exists() && f.delete()) {
                        deletedSize += file.size
                    }
                } catch (e: Exception) {
                    // Skip files that can't be deleted
                }
            }

            handler.post {
                // Navigate to NcEnd with clean_size parameter
                val intent = Intent(this, NcEnd::class.java).apply {
                    putExtra("clean_size", deletedSize.toString())
                    putExtra("page_type", "duplicate")
                }
                startActivity(intent)
                finish()
            }
        }.start()
    }
}