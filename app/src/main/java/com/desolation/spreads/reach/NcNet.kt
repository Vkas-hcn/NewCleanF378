package com.desolation.spreads.reach

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.desolation.spreads.reach.databinding.NcMasBinding
import com.desolation.spreads.reach.databinding.NcNetBinding

class NcNet : AppCompatActivity() {
    private val binding by lazy { NcNetBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.net)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        showNetTool()
    }

    private fun showNetTool() {
        with(binding) {
            appCompatTextView.setOnClickListener {
                finish()
            }
            tvPolicy.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = "https://www.google.com".toUri()
                startActivity(intent)
            }
        }
    }


}