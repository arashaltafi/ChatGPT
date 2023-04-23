package com.arash.altafi.chatgptsimple.ui.image

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arash.altafi.chatgptsimple.BuildConfig
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.ActivityImageSearchBinding
import com.arash.altafi.chatgptsimple.utils.NetworkUtils
import com.arash.altafi.chatgptsimple.utils.toGone
import com.arash.altafi.chatgptsimple.utils.toShow
import com.arash.altafi.chatgptsimple.utils.toast
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ImageSearchActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityImageSearchBinding.inflate(layoutInflater)
    }

    private var client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val json: MediaType = "application/json; charset=utf-8".toMediaType()

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

        btnGenerate.setOnClickListener {
            if (checkNetWork()) {
                val question = edtImage.text.toString().trim()
                edtImage.setText("")
                if (question.isEmpty()) {
                    toast("Please type something")
                    edtImage.error = "Please type something"
                } else {
                    callAPI(question)
                }
            } else {
                toast("Please Turn On Your Internet!!!")
            }
        }

    }

    private fun callAPI(text: String) {
        setInProgress(true)
        val jsonBody = JSONObject()
        try {
            jsonBody.put("prompt", text)
            jsonBody.put("size", "256x256")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val requestBody = jsonBody.toString().toRequestBody(json)
        val request: Request = Request.Builder()
            .url(BuildConfig.OPENAI_URL_IMAGE)
            .header("Authorization", "Bearer ${BuildConfig.TOKEN}")
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                toast("Failed to generate image")
                setInProgress(false)
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let {
                    try {
                        val jsonObject = JSONObject(it.string())
                        val imageUrl = jsonObject
                            .getJSONArray("data")
                            .getJSONObject(0)
                            .getString("url")
                        loadImage(imageUrl)
                        setInProgress(false)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        setInProgress(false)
                    }
                } ?: kotlin.run {
                    setInProgress(false)
                }
            }
        })
    }

    private fun setInProgress(inProgress: Boolean) = binding.apply {
        runOnUiThread {
            if (inProgress) {
                progressbar.toShow()
                btnGenerate.toGone()
            } else {
                progressbar.toGone()
                btnGenerate.toShow()
            }
        }
    }

    private fun loadImage(url: String) {
        runOnUiThread {
            Glide.with(this).load(url).into(binding.ivShow)
        }
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