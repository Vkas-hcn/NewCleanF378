package com.desolation.spreads.reach

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.desolation.spreads.reach.databinding.NcNameBinding
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NcNameFall : AppCompatActivity() {
    // 使用伴生对象保存常量
    companion object {
        private const val GUIDE_DELAY_MS = 1500L
        private val MAIN_DISPATCHER: CoroutineDispatcher = Dispatchers.Main
    }
    
    // 使用懒加载属性不同的语法
    private val binding: NcNameBinding by lazy(LazyThreadSafetyMode.NONE) {
        NcNameBinding.inflate(layoutInflater)
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupWindow()
        setupUI()
        setupListeners()
    }
    
    // 拆分为多个私有方法
    private fun setupWindow() {
        enableEdgeToEdge()
        setContentView(binding.root)
        applyWindowInsets()
    }
    
    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.guide)) {
            view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    
    private fun setupUI() {
        // 保持原有的UI设置
    }
    
    private fun setupListeners() {
        // 添加空的返回按钮回调
        onBackPressedDispatcher.addCallback(this) { /* 空实现 */ }
        showGuide()
    }
    
    // 使用不同的协程启动方式
    fun showGuide() {
        val intent = Intent(this, NcMaster::class.java)
        
        lifecycleScope.launch(MAIN_DISPATCHER) {
            delay(GUIDE_DELAY_MS)
            startActivity(intent)
            finish()
        }
    }
}