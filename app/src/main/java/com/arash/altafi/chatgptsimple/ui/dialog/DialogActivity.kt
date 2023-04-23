package com.arash.altafi.chatgptsimple.ui.dialog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.chatgptsimple.databinding.ActivityDialogBinding

class DialogActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDialogBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        init()
    }

    private fun init() = binding.apply {

    }

}