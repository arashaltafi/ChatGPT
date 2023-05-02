package com.arash.altafi.chatgptsimple.ui.image

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.FragmentImageSearchBinding
import com.arash.altafi.chatgptsimple.ext.*
import com.arash.altafi.chatgptsimple.utils.NetworkUtils
import com.arash.altafi.chatgptsimple.utils.WindowInsetsHelper
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ImageSearchFragment : Fragment() {

    private val binding by lazy {
        FragmentImageSearchBinding.inflate(layoutInflater)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserve()
        init()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        handleKeyboardSize()
        registerNetworkConnectivity(requireContext())
        return binding.root
    }

    private fun handleKeyboardSize() {
        val windowInsetsHelper = WindowInsetsHelper(requireActivity().window, binding.root)
        windowInsetsHelper.isFullScreen = false
        windowInsetsHelper.isAutoResizeKeyboard = false
    }

    private fun init() = binding.apply {
        //first time (before change network)
        changeIconStatus(checkNetWork())

        networkConnection = {
            CoroutineScope(Dispatchers.Main).launch {
                changeIconStatus(it)
            }
        }

        val background = if (requireActivity().isDarkTheme()) {
            ContextCompat.getDrawable(requireContext(), R.drawable.chat_bg_dark)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.chat_bg_light)
        }
        root.background = background

        btnGenerate.setOnClickListener {
            if (checkNetWork()) {
                val question = edtImage.text.toString().trim()
                edtImage.setText("")
                if (question.isEmpty()) {
                    toast(getString(R.string.please_fill_type))
                    edtImage.error = getString(R.string.please_fill_type)
                } else {
                    it.hideKeyboard()
                    callAPI(question)
                }
            } else {
                toast(getString(R.string.network_down))
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
    }

    private fun initObserve() {
        viewModel.liveDataImage.observe(viewLifecycleOwner) { response ->
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

        binding.ivShow.setOnClickListener {
            findNavController().navigate(
                ImageSearchFragmentDirections.actionImageSearchFragmentToImageFragment(
                    url
                )
            )
        }
    }

    private fun checkNetWork() = NetworkUtils.isConnected(requireContext())

    @SuppressLint("ResourceAsColor")
    private fun changeIconStatus(isConnect: Boolean) {
        val red =
            ContextCompat.getColor(requireContext(), R.color.dark_red)
        val green =
            ContextCompat.getColor(requireContext(), R.color.green)
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
        unregisterNetworkConnectivity(requireContext())
    }

}