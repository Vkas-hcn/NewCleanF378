package com.des.show.bee

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.des.show.bee.databinding.NcNetBinding

class NcNet : AppCompatActivity() {
    // 使用伴生对象存储常量
    companion object {
        private const val GOOGLE_URL = "https://sites.google.com/view/smooth-clean/home"
    }
    
    // 使用不同的懒加载初始化方式
    private lateinit var binding: NcNetBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = NcNetBinding.inflate(layoutInflater)
        
        // 重新组织初始化顺序
        enableEdgeToEdge()
        setContentView(binding.root)
        
        // 应用窗口边距
        setupWindowInsets()
        
        // 初始化工具
        initializeNetTool()
    }
    
    // 提取为独立方法
    private fun setupWindowInsets() {
        val rootView = findViewById<android.view.View>(R.id.net)
        
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            
            // 使用扩展函数设置内边距
            view.applySystemWindowInsets(insets)
            
            // 返回修改后的insets
            windowInsets
        }
    }
    
    // 添加扩展函数
    private fun android.view.View.applySystemWindowInsets(insets: Insets) {
        this.setPadding(
            insets.left,
            insets.top,
            insets.right,
            insets.bottom
        )
    }
    
    // 使用不同的方法名和实现方式
    private fun initializeNetTool() {
        // 分开设置监听器而不是使用with
        binding.appCompatTextView.setOnClickListener {
            handleBackButtonClick()
        }
        
        binding.tvPolicy.setOnClickListener {
            handlePolicyClick()
        }
    }
    
    // 提取具体操作到单独方法
    private fun handleBackButtonClick() {
        finish()
    }
    
    private fun handlePolicyClick() {
        // 使用Uri.parse替代toUri扩展函数
        val policyUri = Uri.parse(GOOGLE_URL)
        val intent = Intent(Intent.ACTION_VIEW, policyUri)
        startActivity(intent)
    }
}