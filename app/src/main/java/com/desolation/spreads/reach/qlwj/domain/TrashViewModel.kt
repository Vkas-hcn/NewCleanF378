package com.desolation.spreads.reach.qlwj.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.desolation.spreads.reach.qlwj.data.TrashRepository
import com.desolation.spreads.reach.qlwj.data.model.TrashCategory
import kotlinx.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean


class TrashViewModel : ViewModel() {

    // 数据层
    private val repository = TrashRepository()

    // UI状态
    private val _isScanning = MutableLiveData(false)
    val isScanning: LiveData<Boolean> = _isScanning

    private val _scanningPath = MutableLiveData("")
    val scanningPath: LiveData<String> = _scanningPath

    private val _progress = MutableLiveData(0)
    val progress: LiveData<Int> = _progress

    private val _totalTrashSize = MutableLiveData(0L)
    val totalTrashSize: LiveData<Long> = _totalTrashSize

    private val _trashCategories = MutableLiveData<List<TrashCategory>>(emptyList())
    val trashCategories: LiveData<List<TrashCategory>> = _trashCategories

    private val _isCleaning = MutableLiveData(false)
    val isCleaning: LiveData<Boolean> = _isCleaning

    private val _cleaningProgress = MutableLiveData<Pair<Int, Int>>(Pair(0, 0))
    val cleaningProgress: LiveData<Pair<Int, Int>> = _cleaningProgress

    private val _cleanResult = MutableLiveData<Pair<Int, Long>?>(null)
    val cleanResult: LiveData<Pair<Int, Long>?> = _cleanResult

    // 扫描控制标志
    private val shouldContinueScanning = AtomicBoolean(true)
    private var scanJob: Job? = null


    fun setAppDirectories(cacheDir: java.io.File?, externalCacheDir: java.io.File?) {
        repository.setContextDirectories(cacheDir, externalCacheDir)
    }


    fun startScanning() {
        if (_isScanning.value == true) return

        _isScanning.value = true
        _progress.value = 0
        _totalTrashSize.value = 0L
        shouldContinueScanning.set(true)

        scanJob = viewModelScope.launch(Dispatchers.IO) {
            // 记录扫描开始时间
            val scanStartTime = System.currentTimeMillis()
            val minimumScanDuration = 3000L // 最小扫描持续时间（毫秒）

            val categories = repository.scanForTrashFiles(
                progressCallback = { path, progress ->
                    // 计算经过的时间
                    val elapsedTime = System.currentTimeMillis() - scanStartTime
                    // 如果进度太快但时间还不够，减慢进度更新
                    val adjustedProgress = if (elapsedTime < minimumScanDuration) {
                        (progress * elapsedTime / minimumScanDuration).toInt().coerceAtMost(90) // 最多90%，最后留一点完成时间
                    } else {
                        progress
                    }
                    _scanningPath.postValue(path)
                    _progress.postValue(adjustedProgress)
                },
                onFileFoundCallback = { _, totalSize ->
                    _totalTrashSize.postValue(totalSize)
                },
                shouldContinueScanning = { shouldContinueScanning.get() }
            )

            // 确保扫描至少持续指定的时间
            val elapsedTime = System.currentTimeMillis() - scanStartTime
            if (elapsedTime < minimumScanDuration) {
                delay(minimumScanDuration - elapsedTime)
            }

            withContext(Dispatchers.Main) {
                _trashCategories.value = categories
                // 扫描完成后默认选中所有文件
                if (categories.isNotEmpty()) {
                    selectAllFiles()
                }
                _progress.postValue(100) // 确保进度条显示为100%
                _isScanning.value = false
                _scanningPath.value = if (categories.isNotEmpty()) "Scan completed" else "No trash files found"
            }
        }
    }


    fun stopScanning() {
        shouldContinueScanning.set(false)
        scanJob?.cancel()
        _isScanning.value = false
    }


    fun toggleCategoryExpansion(categoryIndex: Int) {
        val categories = _trashCategories.value?.toMutableList() ?: return
        categories[categoryIndex].isExpanded = !categories[categoryIndex].isExpanded
        _trashCategories.value = categories
    }


    fun toggleCategorySelection(categoryIndex: Int) {
        val categories = _trashCategories.value?.toMutableList() ?: return
        val category = categories[categoryIndex]
        category.isSelected = !category.isSelected
        category.files.forEach { it.isSelected = category.isSelected }
        _trashCategories.value = categories
    }


    fun toggleFileSelection(categoryIndex: Int, fileIndex: Int) {
        val categories = _trashCategories.value?.toMutableList() ?: return
        val file = categories[categoryIndex].files[fileIndex]
        file.isSelected = !file.isSelected
        
        // 更新分类的选中状态
        updateCategorySelection(categories[categoryIndex])
        _trashCategories.value = categories
    }


    private fun updateCategorySelection(category: TrashCategory) {
        category.isSelected = category.files.isNotEmpty() && category.files.all { it.isSelected }
    }


    fun hasSelectedFiles(): Boolean {
        return _trashCategories.value?.any {
            category -> category.files.any { it.isSelected }
        } ?: false
    }


    fun cleanSelectedFiles() {
        val categories = _trashCategories.value ?: return
        val selectedFiles = categories.flatMap { category ->
            category.files.filter { it.isSelected }
        }

        if (selectedFiles.isEmpty()) return

        _isCleaning.value = true
        _cleaningProgress.value = Pair(0, selectedFiles.size)

        viewModelScope.launch(Dispatchers.IO) {
            val (deletedCount, deletedSize) = repository.cleanSelectedFiles(
                selectedFiles = selectedFiles,
                onProgressCallback = { current, total ->
                    _cleaningProgress.postValue(Pair(current, total))
                }
            )

            withContext(Dispatchers.Main) {
                _cleanResult.value = Pair(deletedCount, deletedSize)
                _isCleaning.value = false
            }
        }
    }


    fun clearCleanResult() {
        _cleanResult.value = null
    }


    fun selectAllFiles() {
        val categories = _trashCategories.value?.toMutableList() ?: return
        categories.forEach {
            category ->
            category.isSelected = true
            category.files.forEach { it.isSelected = true }
        }
        _trashCategories.value = categories
    }




    override fun onCleared() {
        super.onCleared()
        stopScanning()
    }
}