package com.arash.altafi.chatgptsimple.ui.image

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.chatgptsimple.databinding.ActivityImageBinding
import com.arash.altafi.chatgptsimple.ext.openDownloadURL
import com.bumptech.glide.Glide

class ImageActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityImageBinding.inflate(layoutInflater)
    }

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        url = intent.getStringExtra("IMAGE_URL").toString()
        init()
    }

    private fun init() = binding.apply {
        ivImage.apply {
            Glide.with(this@ImageActivity).load(url).into(ivImage)
            root.postDelayed({
                setMaxZoom(2.5F)
            }, 200)
        }

        ivDownload.setOnClickListener {
            openDownloadURL(url)
        }
    }

}