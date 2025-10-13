package com.desolation.spreads.reach.qlwj.presentation

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.desolation.spreads.reach.NcEnd
import com.desolation.spreads.reach.R
import com.desolation.spreads.reach.qlwj.domain.TrashViewModel
import com.desolation.spreads.reach.qlwj.presentation.adapter.CategoryAdapter
import com.desolation.spreads.reach.qlwj.util.FileSizeFormatter
import com.desolation.spreads.reach.databinding.NcTrashBinding


class NcTrans : AppCompatActivity() {
    private val binding by lazy { NcTrashBinding.inflate(layoutInflater) }
    private lateinit var viewModel: TrashViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        setupWindowInsets()
        setupActionBar()
        setupViewModel()
        setupViews()
        setupObservers()
    }


    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.scan)) {
                v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


    private fun setupActionBar() {
        supportActionBar?.hide()
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this)[TrashViewModel::class.java]
        // 设置应用目录，用于文件扫描
        viewModel.setAppDirectories(cacheDir, externalCacheDir)
    }


    private fun setupViews() {
        // 返回按钮点击事件
        binding.tvBack.setOnClickListener {
            finish()
        }

        // 初始化适配器
        categoryAdapter = CategoryAdapter(
            emptyList(),
            onCategoryExpansionChanged = { viewModel.toggleCategoryExpansion(it) },
            onCategorySelectionChanged = { viewModel.toggleCategorySelection(it) },
            onFileSelectionChanged = { categoryIndex, fileIndex ->
                viewModel.toggleFileSelection(categoryIndex, fileIndex)
            }
        )

        // 设置RecyclerView
        binding.rvCategories.apply {
            layoutManager = LinearLayoutManager(this@NcTrans)
            adapter = categoryAdapter
        }

        // 清理按钮点击事件
        binding.btnCleanNow.setOnClickListener {
            cleanSelectedFiles()
        }

        // 初始UI状态
        binding.progressScaning.visibility = View.GONE
        binding.btnCleanNow.visibility = View.GONE
        updateTrashSize(0L)
    }


    private fun setupObservers() {
        // 监听扫描状态
        viewModel.isScanning.observe(this) {
            if (it) {
                binding.progressScaning.visibility = View.VISIBLE
                binding.progressScaning.progress = 0
                binding.btnCleanNow.visibility = View.GONE
            } else {
                binding.progressScaning.visibility = View.GONE
                // 检查是否有扫描结果，有则显示清理按钮
                if (viewModel.trashCategories.value?.isNotEmpty() == true) {
                    binding.btnCleanNow.visibility = View.VISIBLE
                }
            }
        }

        // 监听扫描路径
        viewModel.scanningPath.observe(this) {
            binding.tvScanningPath.text = "Scanning: $it"
        }

        // 监听扫描进度
        viewModel.progress.observe(this) {
            binding.progressScaning.progress = it
        }

        // 监听总垃圾大小
        viewModel.totalTrashSize.observe(this) {
            updateTrashSize(it)
            // 如果有垃圾文件，更改背景图标
            if (it > 0) {
                binding.imgScanBg.setImageResource(R.drawable.trach_icon)
            }
        }

        // 监听垃圾文件分类列表
        viewModel.trashCategories.observe(this) {
            categories ->
            categoryAdapter.updateCategories(categories)
            updateCleanButtonState()
        }

        // 监听清理状态
        viewModel.isCleaning.observe(this) {
            if (it) {
                binding.progressScaning.visibility = View.VISIBLE
                binding.btnCleanNow.visibility = View.GONE
            } else {
                binding.progressScaning.visibility = View.GONE
            }
        }

        // 监听清理进度
        viewModel.cleaningProgress.observe(this) {
            (current, total) ->
            if(current>0){
                binding.progressScaning.progress = (current * 100) / total
                binding.tvScanningPath.text = "Cleaning: $current/$total files"
            }
        }

        // 监听清理结果
        viewModel.cleanResult.observe(this) {
            it?.let {
                (_, deletedSize) ->
                navigateToResultScreen(deletedSize)
                viewModel.clearCleanResult()
            }
        }
    }


    private fun updateTrashSize(size: Long) {
        val (displaySize, unit) = FileSizeFormatter.formatFileSizeToParts(size)
        binding.tvScannedSize.text = displaySize
        binding.tvScannedSizeUn.text = unit
    }


    private fun updateCleanButtonState() {
        binding.btnCleanNow.isEnabled = viewModel.hasSelectedFiles()
    }


    private fun cleanSelectedFiles() {
        if (!viewModel.hasSelectedFiles()) {
            Toast.makeText(this, "Please select the file to clean", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.cleanSelectedFiles()
    }


    private fun navigateToResultScreen(deletedSize: Long) {
        val intent = Intent(this, NcEnd::class.java).apply {
            putExtra("clean_size", deletedSize.toString())
            putExtra("page_type", "clean")
        }
        startActivity(intent)
        finish()
    }


    override fun onResume() {
        super.onResume()
        if (viewModel.trashCategories.value.isNullOrEmpty() && !viewModel.isScanning.value!!) {
            viewModel.startScanning()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopScanning()
    }
}