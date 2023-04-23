package com.arash.altafi.chatgptsimple.ui.dialog

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.data.local.MessengerDao
import com.arash.altafi.chatgptsimple.data.local.MessengerDatabase
import com.arash.altafi.chatgptsimple.databinding.ActivityDialogBinding
import com.arash.altafi.chatgptsimple.ui.chat.ChatActivity
import com.arash.altafi.chatgptsimple.utils.NetworkUtils
import com.arash.altafi.chatgptsimple.utils.toGone
import com.arash.altafi.chatgptsimple.utils.toShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DialogActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDialogBinding.inflate(layoutInflater)
    }
    private var dialogAdapter: DialogAdapter? = null

    private var messengerDatabase: MessengerDatabase? = null
    private var messengerDao: MessengerDao? = null

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

        messengerDatabase = MessengerDatabase.getAppDataBase(this@DialogActivity)
        messengerDao = messengerDatabase?.MessengerDao()
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

        flNewChat.setOnClickListener {
            startActivity(Intent(this@DialogActivity, ChatActivity::class.java))
        }
    }

    private fun handleList() = binding.apply {
        val dialogListEntity = messengerDao?.getAllDialog()
        if (dialogListEntity?.isEmpty() == true) {
            lottieEmpty.toShow()
        } else {
            lottieEmpty.toGone()
            dialogAdapter = DialogAdapter(ArrayList(dialogListEntity!!))
            rvDialogs.adapter = dialogAdapter
        }
    }

    private fun checkNetWork() = NetworkUtils.isConnected(this@DialogActivity)

    private fun changeIconStatus(isConnect: Boolean) = binding.apply {
        val text = if (isConnect) R.string.app_name else R.string.connecting
        tvToolbarState.text = getString(text)
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

    override fun onResume() {
        super.onResume()
        handleList()
    }

}