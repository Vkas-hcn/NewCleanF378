package com.desolation.spreads.reach

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.desolation.spreads.reach.databinding.NcFeBinding
import com.desolation.spreads.reach.yy.adapter.FileCleanAdapter
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class NcWen : AppCompatActivity() {
    private val binding by lazy { NcFeBinding.inflate(layoutInflater) }

    private val handler = Handler(Looper.getMainLooper())

    // full list (no filters) and currently displayed list
    private val fullFiles = mutableListOf<FileItem>()
    private val displayFiles = mutableListOf<FileItem>()

    private lateinit var adapter: FileCleanAdapter

    // filters
    private var typeFilter: String = "All Type"
    private var sizeFilterBytes: Long = 0L // 0 means all
    private var timeFilterMillis: Long = 0L // 0 means all

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
        showLoadingThenScan()
    }

    private fun setupViews() {
        binding.textBack.setOnClickListener { finish() }

        adapter = FileCleanAdapter(displayFiles) { updateDeleteButtonState() }
        binding.rvFiles.layoutManager = LinearLayoutManager(this)
        binding.rvFiles.adapter = adapter

        binding.tvType.setOnClickListener { showTypeMenu(it) }
        binding.tvSize.setOnClickListener { showSizeMenu(it) }
        binding.tvTime.setOnClickListener { showTimeMenu(it) }

        binding.btnDelete.setOnClickListener {
            val selected = displayFiles.filter { it.isSelected }
            if (selected.isEmpty()) return@setOnClickListener

            AlertDialog.Builder(this)
                .setTitle("Delete files")
                .setMessage("Are you sure to delete ${selected.size} files?")
                .setPositiveButton("Delete") { _, _ -> performDelete(selected) }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun showLoadingThenScan() {
        binding.inLoad.tvTip.text = "Scanning..."
        binding.inLoad.root.setOnClickListener {  }
        binding.inLoad.imgBack.setOnClickListener { finish() }
        binding.inLoad.root.isVisible = true

        val rotate = RotateAnimation(0f, 360f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f,
            RotateAnimation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 800
            repeatCount = RotateAnimation.INFINITE
            interpolator = LinearInterpolator()
        }
        binding.inLoad.imgLoad.startAnimation(rotate)

        handler.postDelayed({
            // stop animation and hide
            binding.inLoad.imgLoad.clearAnimation()
            binding.inLoad.root.isVisible = false
            // start scanning in background
            scanFilesAsync()
        }, 1000)
    }

    private fun scanFilesAsync() {
        Thread {
            val found = scanForFiles()
            // de-duplicate by path
            val unique = LinkedHashMap<String, FileItem>()
            for (f in found) {
                if (!unique.containsKey(f.path)) unique[f.path] = f
            }

            fullFiles.clear()
            fullFiles.addAll(unique.values)

            // default sort: newest first
            fullFiles.sortByDescending { it.lastModified }

            handler.post {
                applyFiltersAndRefresh()
            }
        }.start()
    }

    private fun scanForFiles(): List<FileItem> {
        val results = ArrayList<FileItem>()
        try {
            val roots = mutableListOf<File>()
            Environment.getExternalStorageDirectory()?.let { roots.add(it) }
            // add common directories
            val download = File(Environment.getExternalStorageDirectory(), "Download")
            if (download.exists()) roots.add(download)

            // walk up to a limited depth to avoid ANR
            val maxDepth = 6

            val visited = HashSet<String>()

            fun walk(dir: File, depth: Int) {
                if (!dir.exists() || !dir.canRead()) return
                if (depth > maxDepth) return
                val files = dir.listFiles() ?: return
                for (f in files) {
                    try {
                        val p = f.absolutePath
                        if (visited.contains(p)) continue
                        visited.add(p)
                        if (f.isDirectory) {
                            walk(f, depth + 1)
                        } else {
                            results.add(FileItem(
                                name = f.name,
                                path = f.absolutePath,
                                size = f.length(),
                                lastModified = f.lastModified()
                            ))
                        }
                    } catch (_: Exception) {}
                }
            }

            for (r in roots) walk(r, 0)
        } catch (_: Exception) {
        }
        return results
    }

    private fun showTypeMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        val types = listOf("All Type", "Image", "Video", "Audio", "Docs", "Download", "Zip")
        types.forEachIndexed { i, t -> popup.menu.add(0, i, i, t) }
        popup.setOnMenuItemClickListener { item: MenuItem ->
            typeFilter = item.title.toString()
            binding.tvType.text = typeFilter
            applyFiltersAndRefresh()
            true
        }
        popup.show()
    }

    private fun showSizeMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        val items = listOf("All Size", ">10MB", ">20MB", ">50MB", ">100MB", ">200MB", ">500MB")
        items.forEachIndexed { i, t -> popup.menu.add(0, i, i, t) }
        popup.setOnMenuItemClickListener { item: MenuItem ->
            val text = item.title.toString()
            binding.tvSize.text = text
            sizeFilterBytes = when (text) {
                "All Size" -> 0L
                ">10MB" -> 10L * 1024 * 1024
                ">20MB" -> 20L * 1024 * 1024
                ">50MB" -> 50L * 1024 * 1024
                ">100MB" -> 100L * 1024 * 1024
                ">200MB" -> 200L * 1024 * 1024
                ">500MB" -> 500L * 1024 * 1024
                else -> 0L
            }
            applyFiltersAndRefresh()
            true
        }
        popup.show()
    }

    private fun showTimeMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        val items = listOf("All Time", "Within 1 day", "Within 1 week", "Within 1 month", "Within 3 month", "Within 6 month")
        items.forEachIndexed { i, t -> popup.menu.add(0, i, i, t) }
        popup.setOnMenuItemClickListener { item: MenuItem ->
            val text = item.title.toString()
            binding.tvTime.text = text
            val now = System.currentTimeMillis()
            timeFilterMillis = when (text) {
                "All Time" -> 0L
                "Within 1 day" -> now - 1L * 24 * 60 * 60 * 1000
                "Within 1 week" -> now - 7L * 24 * 60 * 60 * 1000
                "Within 1 month" -> now - 30L * 24 * 60 * 60 * 1000
                "Within 3 month" -> now - 90L * 24 * 60 * 60 * 1000
                "Within 6 month" -> now - 180L * 24 * 60 * 60 * 1000
                else -> 0L
            }
            applyFiltersAndRefresh()
            true
        }
        popup.show()
    }

    private fun applyFiltersAndRefresh() {
        displayFiles.clear()
        val now = System.currentTimeMillis()
        for (f in fullFiles) {
            if (!matchesType(f, typeFilter)) continue
            if (sizeFilterBytes > 0 && f.size <= sizeFilterBytes) continue
            if (timeFilterMillis > 0 && f.lastModified < timeFilterMillis) continue
            displayFiles.add(f.copy())
        }
        adapter.notifyDataSetChanged()
        updateDeleteButtonState()
    }

    private fun matchesType(f: FileItem, type: String): Boolean {
        if (type == "All Type") return true
        val lower = f.name.lowercase(Locale.getDefault())
        return when (type) {
            "Image" -> listOf(".jpg", ".jpeg", ".png", ".gif", ".bmp", ".webp", ".heic", ".heif").any { lower.endsWith(it) }
            "Video" -> listOf(".mp4", ".mkv", ".mov", ".avi", ".rmvb", ".3gp", ".mpeg").any { lower.endsWith(it) }
            "Audio" -> listOf(".mp3", ".wav", ".m4a", ".flac", ".aac", ".ogg").any { lower.endsWith(it) }
            "Docs" -> listOf(".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt").any { lower.endsWith(it) }
            "Download" -> f.path.contains("/Download") || f.path.contains("/download")
            "Zip" -> listOf(".zip", ".rar", ".7z", ".tar", ".gz").any { lower.endsWith(it) }
            else -> true
        }
    }

    private fun updateDeleteButtonState() {
        val hasSelected = displayFiles.any { it.isSelected }
        binding.btnDelete.isEnabled = hasSelected
    }

    private fun performDelete(selected: List<FileItem>) {
        Thread {
            var deletedSize = 0L
            for (s in selected) {
                try {
                    val f = File(s.path)
                    if (f.exists() && f.delete()) deletedSize += s.size
                } catch (_: Exception) {
                }
            }

            // After deleting, rescan or remove from lists
            // For speed, remove paths from fullFiles
            val removedPaths = selected.map { it.path }.toSet()
            fullFiles.removeAll { removedPaths.contains(it.path) }

            handler.post {
                applyFiltersAndRefresh()
                // go to NcEnd and pass clean_size as bytes string
                val intent = Intent(this, NcEnd::class.java).apply {
                    putExtra("clean_size", deletedSize.toString())
                }
                startActivity(intent)
                finish()
            }
        }.start()
    }

}