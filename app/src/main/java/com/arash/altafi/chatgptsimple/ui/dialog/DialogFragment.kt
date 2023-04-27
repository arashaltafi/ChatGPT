package com.arash.altafi.chatgptsimple.ui.dialog

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arash.altafi.chatgptsimple.databinding.FragmentDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.arash.altafi.chatgptsimple.BuildConfig
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntity
import com.arash.altafi.chatgptsimple.ext.toGone
import com.arash.altafi.chatgptsimple.ext.toShow
import com.arash.altafi.chatgptsimple.ext.toast
import com.arash.altafi.chatgptsimple.utils.Cache
import com.arash.altafi.chatgptsimple.utils.NetworkUtils
import com.arash.altafi.chatgptsimple.utils.PopupUtil
import com.arash.altafi.chatgptsimple.utils.WindowInsetsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DialogFragment : Fragment() {

    private val binding by lazy {
        FragmentDialogBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var cache: Cache

    private val viewModel: DialogViewModel by viewModels()

    private lateinit var dialogAdapter: DialogAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        handleKeyboardSize()
        return binding.root
    }

    private fun handleKeyboardSize() {
        val windowInsetsHelper = WindowInsetsHelper(requireActivity().window, binding.root)
        windowInsetsHelper.isFullScreen = false
        windowInsetsHelper.isAutoResizeKeyboard = false
    }

    private fun init() = binding.apply {
        cache.tokenAES = BuildConfig.TOKEN

        registerNetworkConnectivity(requireContext())

        //first time (before change network)
        changeIconStatus(checkNetWork())

        networkConnection = {
            CoroutineScope(Dispatchers.Main).launch {
                changeIconStatus(it)
            }
        }

        flNewChat.setOnClickListener {
            findNavController().navigate(
                DialogFragmentDirections.actionDialogFragmentToChatFragment()
            )
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
                    findNavController().navigate(
                        DialogFragmentDirections.actionDialogFragmentToImageSearchFragment()
                    )
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

            val swipeHandler =
                object : SwipeToDeleteCallbackObjectBox(viewModel, dialogAdapter) {}
            val itemTouchHelper = ItemTouchHelper(swipeHandler)
            itemTouchHelper.attachToRecyclerView(rvDialogs)

            dialogAdapter.onClickListener = {
                findNavController().navigate(
                    DialogFragmentDirections.actionDialogFragmentToChatFragment(it.id!!)
                )
            }

            dialogAdapter.onLongClickListener = { view, dialogModel ->
                popupWindowAdapter(view, dialogModel)
            }
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

    private fun checkNetWork() = NetworkUtils.isConnected(requireContext())

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
        unregisterNetworkConnectivity(requireContext())
    }

    override fun onResume() {
        super.onResume()
        handleList()
    }

}