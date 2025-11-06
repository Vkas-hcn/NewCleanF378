package com.des.show.bee

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NcJump : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, NcNameFall::class.java))
        finish()
    }
}