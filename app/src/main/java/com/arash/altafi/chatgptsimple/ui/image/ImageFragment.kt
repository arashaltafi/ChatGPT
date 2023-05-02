package com.arash.altafi.chatgptsimple.ui.image

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.arash.altafi.chatgptsimple.databinding.FragmentImageBinding
import com.arash.altafi.chatgptsimple.ext.openDownloadURL
import com.arash.altafi.chatgptsimple.utils.WindowInsetsHelper
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageFragment : Fragment() {

    private val binding by lazy {
        FragmentImageBinding.inflate(layoutInflater)
    }

    private val args by navArgs<ImageFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        handleKeyboardSize()
        init(args.url)
        return binding.root
    }

    private fun handleKeyboardSize() {
        val windowInsetsHelper = WindowInsetsHelper(requireActivity().window, binding.root)
        windowInsetsHelper.isFullScreen = false
        windowInsetsHelper.isAutoResizeKeyboard = false
    }

    private fun init(url: String) = binding.apply {
        ivImage.apply {
            Glide.with(requireContext()).load(url).into(ivImage)
            root.postDelayed({
                setMaxZoom(2.5F)
            }, 200)
        }

        ivDownload.setOnClickListener {
            requireContext().openDownloadURL(url)
        }
    }

}