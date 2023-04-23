package com.arash.altafi.chatgptsimple.ui.image

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.ActivityImageSearchBinding
import com.arash.altafi.chatgptsimple.ext.*
import com.arash.altafi.chatgptsimple.utils.*
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*

@AndroidEntryPoint
class ImageSearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityImageSearchBinding.inflate(layoutInflater)
    }

    private val viewModel: ImageViewModel by viewModels()

    private var networkConnection: ((connected: Boolean) -> Unit)? = null

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            networkConnection?.invoke(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            networkConnection?.invoke(false)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        registerNetworkConnectivity(this)
        init()
    }

    private fun init() = binding.apply {
        //first time (before change network)
        changeIconStatus(checkNetWork())

        networkConnection = {
            CoroutineScope(Dispatchers.Main).launch {
                changeIconStatus(it)
            }
        }

        val background = if (isDarkTheme()) {
            ContextCompat.getDrawable(this@ImageSearchActivity, R.drawable.chat_bg_dark)
        } else {
            ContextCompat.getDrawable(this@ImageSearchActivity, R.drawable.chat_bg_light)
        }
        root.background = background

        btnGenerate.setOnClickListener {
            if (checkNetWork()) {
                val question = edtImage.text.toString().trim()
                edtImage.setText("")
                if (question.isEmpty()) {
                    toast("Please type something")
                    edtImage.error = "Please type something"
                } else {
                    it.hideKeyboard()
                    callAPI(question)
                }
            } else {
                toast("Please Turn On Your Internet!!!")
            }
        }

        edtImage.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    btnGenerate.performClick()
                }
            }
            false
        }
    }

    private fun callAPI(text: String) {
        setInProgress(true)
        viewModel.generateImage(text)

        viewModel.liveDataImage.observe(this) { response ->
            response.data?.get(0)?.url?.let {
                loadImage(it)
            }
            setInProgress(false)
        }
    }

    private fun setInProgress(inProgress: Boolean) = binding.apply {
        if (inProgress) {
            progressbar.toShow()
            btnGenerate.toGone()
        } else {
            progressbar.toGone()
            btnGenerate.toShow()
        }
    }

    private fun loadImage(url: String) {
        Glide.with(this).load(url).into(binding.ivShow)
    }

    private fun checkNetWork() = NetworkUtils.isConnected(this@ImageSearchActivity)

    @SuppressLint("ResourceAsColor")
    private fun changeIconStatus(isConnect: Boolean) {
        val red =
            ContextCompat.getColor(this, R.color.dark_red)
        val green =
            ContextCompat.getColor(this, R.color.green)
        val color = if (isConnect) green else red
        binding.ivStatus.setColorFilter(color)
    }

    private fun registerNetworkConnectivity(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val builder = NetworkRequest.Builder().build()
            connectivityManager.registerNetworkCallback(builder, networkCallback)
        }
    }

    private fun unregisterNetworkConnectivity(context: Context) {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkConnectivity(this)
    }

}