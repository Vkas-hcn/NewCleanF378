package com.des.show.bee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.des.show.bee.cf.NcChong
import com.des.show.bee.qlwj.presentation.NcTrans
import com.des.show.bee.databinding.NcEndBinding
import com.des.show.bee.yy.NcAppMc

class NcEnd : AppCompatActivity() {
    private val binding by lazy { NcEndBinding.inflate(layoutInflater) }
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.net)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 获取传递的参数
        val pageType = intent.getStringExtra("page_type") ?: "app"
        val cleanSize = intent.getStringExtra("clean_size") ?: ""

        // 显示加载页面
        showLoadingScreen(pageType)

        // 1秒后隐藏加载页面，显示主页面
        handler.postDelayed({
            hideLoadingScreen()
            setupMainScreen(cleanSize.toLong())
            setupButtonListeners()
        }, 1000)
    }

    private fun showLoadingScreen(pageType: String) {
        // 显示加载页面
        binding.inLoad.missPm.visibility = View.VISIBLE

        // 设置旋转动画
        val rotateAnimation = RotateAnimation(
            0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 1000
            repeatCount = Animation.INFINITE
            fillAfter = true
        }

        // 应用旋转动画到imgLoad
        binding.inLoad.imgLoad.startAnimation(rotateAnimation)

        // 根据page_type设置不同的imgLogo
        val logoResId = when (pageType) {
            "app" -> R.drawable.icon_app_end
            "large" -> R.drawable.icon_large_files_end
            "duplicate" -> R.drawable.dup_icon
            "clean" -> R.drawable.sba
            else -> R.drawable.icon_app_end
        }
        binding.inLoad.imgLogo.setImageResource(logoResId)
    }

    private fun hideLoadingScreen() {
        // 停止动画
        binding.inLoad.imgLoad.clearAnimation()
        // 隐藏加载页面
        binding.inLoad.missPm.visibility = View.GONE
    }

    private fun setupMainScreen(cleanSize: Long) {
        val sizeInMB = formatFileSize(cleanSize).first + formatFileSize(cleanSize).second
        val sizeText = "Saved ${sizeInMB} space for you"
        binding.tvEnd.text = sizeText

        binding.appCompatTextView.setOnClickListener {
            finish()
        }
    }

    private fun formatFileSize(size: Long): Pair<String, String> {
        return when {
            size >= 1024 * 1024 * 1024 -> {
                Pair(String.format("%.2f", size / (1024.0 * 1024.0 * 1024.0)), "GB")
            }

            size >= 1024 * 1024 -> {
                Pair(String.format("%.2f", size / (1024.0 * 1024.0)), "MB")
            }

            else -> {
                Pair(String.format("%.2f", size / 1024.0), "KB")
            }
        }
    }

    private fun setupButtonListeners() {
        binding.inLoad.root.setOnClickListener {

        }
        binding.inLoad.imgBack.setOnClickListener {
            finish()
        }
        // 设置App Manager按钮点击事件
        binding.mbApp.setOnClickListener {
            startActivity(Intent(this, NcAppMc::class.java))
            finish()
        }

        // 设置Large Files按钮点击事件
        binding.mbLarge.setOnClickListener {
            val intent = Intent(this, NcWen::class.java)
            startActivity(intent)
            finish()
        }

        // 设置Duplicate Files按钮点击事件
        binding.mbDuplicate.setOnClickListener {
            val intent = Intent(this, NcChong::class.java)
            startActivity(intent)
            finish()
        }

        // 设置Clean按钮点击事件
        binding.mbClean.setOnClickListener {
            val intent = Intent(this, NcTrans::class.java)
            startActivity(intent)
            finish()
        }
    }
}