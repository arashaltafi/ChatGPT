package com.arash.altafi.chatgptsimple.ui.dialog

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntity
import com.arash.altafi.chatgptsimple.databinding.ActivityDialogBinding
import com.arash.altafi.chatgptsimple.ext.toGone
import com.arash.altafi.chatgptsimple.ext.toShow
import com.arash.altafi.chatgptsimple.ext.toast
import com.arash.altafi.chatgptsimple.ui.chat.ChatActivity
import com.arash.altafi.chatgptsimple.ui.image.ImageSearchActivity
import com.arash.altafi.chatgptsimple.utils.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DialogActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityDialogBinding.inflate(layoutInflater)
    }

    private val viewModel: DialogViewModel by viewModels()

    private var dialogAdapter: DialogAdapter? = null

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

        flNewChat.setOnClickListener {
            val intent = Intent(this@DialogActivity, ChatActivity::class.java).apply {
                putExtra("DialogId", "-1")
            }
            startActivity(intent)
        }

        ivMore.setOnClickListener {
            popupWindow(it)
        }
    }

    private fun popupWindow(view: View) {
        PopupUtil.showPopup(
            view,
            listOf(
                PopupUtil.PopupItem(
                    R.drawable.ic_baseline_image_search_24,
                    getString(R.string.gpt_image)
                ) {
                    startActivity(Intent(this, ImageSearchActivity::class.java))
                }
            ),
            Gravity.BOTTOM.or(Gravity.END),
            setTint = false
        )
    }

    private fun handleList() = binding.apply {
        val dialogListEntity = viewModel.getAllDialog()
        if (dialogListEntity.isEmpty()) {
            lottieEmpty.toShow()
        } else {
            lottieEmpty.toGone()
            dialogAdapter = DialogAdapter(ArrayList(dialogListEntity))
            rvDialogs.adapter = dialogAdapter
        }

        dialogAdapter?.onClickListener = {
            val intent = Intent(this@DialogActivity, ChatActivity::class.java).apply {
                putExtra("DialogId", it.id)
            }
            startActivity(intent)
        }

        dialogAdapter?.onLongClickListener = { view, dialogModel ->
            popupWindowAdapter(view, dialogModel)
        }
    }

    private fun popupWindowAdapter(view: View, dialogModel: DialogEntity) {
        PopupUtil.showPopup(
            view,
            listOf(
                PopupUtil.PopupItem(
                    R.drawable.ic_baseline_delete_24,
                    getString(R.string.delete)
                ) {
                    viewModel.deleteDialog(dialogModel)
                    handleList()
                    toast("SuccessFully Deleted")
                }
            ),
            Gravity.BOTTOM.or(Gravity.END),
            setTint = false
        )
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