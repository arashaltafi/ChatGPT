package com.arash.altafi.chatgptsimple.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.chatgptsimple.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}