package com.arash.altafi.chatgptsimple.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.arash.altafi.chatgptsimple.BuildConfig
import com.arash.altafi.chatgptsimple.databinding.ActivitySplashBinding
import com.arash.altafi.chatgptsimple.ui.chat.ChatActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() = binding.apply {
        tvAppVersion.text = BuildConfig.VERSION_NAME

        root.postDelayed({
            startActivity(Intent(this@SplashActivity, ChatActivity::class.java))
            finish()
        }, 4000)
    }

}