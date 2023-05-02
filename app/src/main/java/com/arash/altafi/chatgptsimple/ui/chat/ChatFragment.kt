package com.arash.altafi.chatgptsimple.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.FragmentChatBinding
import com.arash.altafi.chatgptsimple.domain.model.chat.ChatPostBody
import com.arash.altafi.chatgptsimple.domain.model.chat.ChatRole
import com.arash.altafi.chatgptsimple.domain.model.chat.MessageState
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntityObjectBox
import com.arash.altafi.chatgptsimple.domain.provider.local.MessageEntityObjectBox
import com.arash.altafi.chatgptsimple.ext.*
import com.arash.altafi.chatgptsimple.ui.dialog.DialogViewModel
import com.arash.altafi.chatgptsimple.ui.image.ImageViewModel
import com.arash.altafi.chatgptsimple.utils.NetworkUtils
import com.arash.altafi.chatgptsimple.utils.PopupUtil
import com.arash.altafi.chatgptsimple.utils.WindowInsetsHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import saman.zamani.persiandate.PersianDate

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val binding by lazy {
        FragmentChatBinding.inflate(layoutInflater)
    }

    private val args by navArgs<ChatFragmentArgs>()
    private var dialogId = 0L

    private val dialogViewModel: DialogViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()

    private var isRestoredFromBackStack = false

    private var finalList: ArrayList<MessageEntityObjectBox> = arrayListOf()

    private lateinit var messageAdapter: MessageAdapter

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
        if (!isRestoredFromBackStack)
            init()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isRestoredFromBackStack = false
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
        windowInsetsHelper.isAutoResizeKeyboard = true
    }

    private fun init() = binding.apply {
        if (args.dialogId != -1L) {
            val dialogEntity = dialogViewModel.getDialogByIdObjectBox(args.dialogId)
            dialogEntity!!.messages.forEach {
                finalList.add(it)
            }
            dialogId = dialogEntity.dialogId ?: -1L
        } else {
            dialogId = getLastIdOfDB() + 1
            val messageEntityObjectBox = MessageEntityObjectBox()
            messageEntityObjectBox.message = getString(R.string.welcomeMessage)
            messageEntityObjectBox.sentBy = MessageState.BOT_TEXT.name
            messageEntityObjectBox.time = PersianDate().getClockString()
            finalList.add(messageEntityObjectBox)

            val dialogEntity = DialogEntityObjectBox()
            dialogEntity.dialogId = getLastIdOfDB() + 1
            dialogEntity.lastTime = System.currentTimeMillis()

            finalList.forEach {
                it.dialog.targetId = dialogViewModel.getDialogId(dialogEntity)
            }

            dialogEntity.messages.addAll(finalList)
            dialogViewModel.saveDialogObjectBox(dialogEntity)
        }

        //first time (before change network)
        changeIconStatus(checkNetWork())

        networkConnection = {
            CoroutineScope(Dispatchers.Main).launch {
                changeIconStatus(it)
            }
        }

        val background = if (requireActivity().isDarkTheme())
            ContextCompat.getDrawable(requireContext(), R.drawable.chat_bg_dark)
        else
            ContextCompat.getDrawable(requireContext(), R.drawable.chat_bg_light)

        rlRoot.background = background

        edtMessage.requestFocus()

        messageAdapter = MessageAdapter()
        messageAdapter.submitList(finalList)
        rvChat.adapter = messageAdapter

        messageAdapter.onClickImageListener = {
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToImageFragment(
                    it
                )
            )
        }

        messageAdapter.onClickReplyListener = {
            edtMessage.setText(it)
        }

        messageAdapter.onLongClickListener = { view, point, item, bitmap ->
            popupWindowAdapter(view, point, item, bitmap)
        }

        btnSend.setOnClickListener {
            if (checkNetWork()) {
                val question = edtMessage.text.toString().trim()
                edtMessage.setText("")
                if (question.isEmpty() || question.substringAfter(IMAGE).trim().isEmpty()) {
                    toast(getString(R.string.please_fill_type))
                    edtMessage.error = getString(R.string.please_fill_type)
                } else {
                    it.hideKeyboard()
                    addToChat(question, MessageState.ME)
                    callAPI(question, question.startsWith(IMAGE))
                }
            } else {
                toast(getString(R.string.network_down))
            }
        }

        ivMore.setOnClickListener {
            popupWindow(it)
        }

        edtMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Get the current text in the EditText
                val text = edtMessage.text?.toString() ?: return
                val spannable = edtMessage.editableText

                // Delete "/image" with backSpace
                if (before == 1 && s.toString().endsWith(IMAGE)) {
                    edtMessage.setText(s.toString().substring(0, s!!.length - IMAGE.length))
                    edtMessage.setSelection(edtMessage.length())
                }

                // Check if the text contains the string "/image"
                if (text.startsWith(IMAGE)) {
                    // Find the index of the start of the "/image" string
                    val startIndex = text.indexOf(IMAGE)

                    // Find the index of the end of the "/image" string
                    val endIndex = startIndex + IMAGE.length

                    // Set the color of the "/image" string to red
                    spannable.setSpan(
                        ForegroundColorSpan(Color.RED),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannable.setSpan(
                        BackgroundColorSpan(Color.YELLOW),
                        text.indexOf(IMAGE),
                        startIndex + IMAGE.length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                } else { // Remove the color if the "/image" string is no longer in the text
                    val spans =
                        spannable.getSpans(0, spannable.length, ForegroundColorSpan::class.java)
                    for (span in spans) {
                        spannable.removeSpan(span)
                    }

                    val foregroundSpans =
                        spannable.getSpans(0, spannable.length, ForegroundColorSpan::class.java)
                    for (span in foregroundSpans) {
                        spannable.removeSpan(span)
                    }

                    val backgroundSpans =
                        spannable.getSpans(0, spannable.length, BackgroundColorSpan::class.java)
                    for (span in backgroundSpans) {
                        spannable.removeSpan(span)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        edtMessage.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    btnSend.performClick()
                }
            }
            false
        }
    }

    private fun getLastIdOfDB() = dialogViewModel.getLastDialogIdObjectBox()

    private fun popupWindow(view: View) {
        val list = mutableListOf(
            PopupUtil.PopupItem(
                R.drawable.ic_baseline_arrow_back_24,
                getString(R.string.back)
            ) {
                findNavController().navigateUp()
            },
        )

        if (args.dialogId != -1L) {
            list.add(
                PopupUtil.PopupItem(
                    R.drawable.ic_baseline_delete_24,
                    getString(R.string.delete)
                ) {
                    dialogViewModel.deleteDialogByIdObjectBox(args.dialogId)
                    findNavController().navigateUp()
                }
            )
        }

        PopupUtil.showPopup(
            view,
            list,
            Gravity.BOTTOM.or(Gravity.END)
        )
    }

    private fun popupWindowAdapter(
        view: View,
        point: Point,
        item: MessageEntityObjectBox,
        bitmap: Bitmap? = null
    ) {
        val list = mutableListOf(
            PopupUtil.PopupItem(
                R.drawable.baseline_content_copy_24,
                getString(R.string.copy)
            ) {
                requireContext().copyTextToClipboard(item.message.toString())
            },
            PopupUtil.PopupItem(
                R.drawable.baseline_share_24,
                getString(R.string.share)
            ) {
                bitmap?.let {
                    requireContext().shareImage(it)
                } ?: kotlin.run {
                    requireContext().shareContent(item.message.toString())
                }
            }
        )

        PopupUtil.showPopup(
            view,
            list,
            point
        )
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

    @SuppressLint("NotifyDataSetChanged")
    private fun addToChat(message: String, sentBy: MessageState) {
        val messageEntityObjectBox = MessageEntityObjectBox()
        messageEntityObjectBox.message = message
        messageEntityObjectBox.sentBy = sentBy.name
        messageEntityObjectBox.time = PersianDate().getClockString()
        finalList.add(messageEntityObjectBox)
        messageAdapter.submitList(finalList)
        messageAdapter.notifyDataSetChanged()
        binding.rvChat.smoothScrollToPosition(messageAdapter.itemCount)

        if (sentBy != MessageState.TYPING && sentBy != MessageState.SENDING_IMAGE) {
            val dialogEntity = DialogEntityObjectBox()
            dialogEntity.dialogId = dialogId
            dialogEntity.messages.addAll(finalList)
            dialogEntity.lastTime = System.currentTimeMillis()
            dialogViewModel.updateDialogObjectBox(dialogEntity)
        }
    }

    private fun addResponse(response: String, isImage: Boolean) = binding.apply {
        finalList.removeAt(finalList.size - 1)
        addToChat(response, if (isImage) MessageState.BOT_IMAGE else MessageState.BOT_TEXT)
        tvToolbarState.toGone()
        btnSend.toShow()
        progressBar.toHide()
    }

    private fun callAPI(question: String, isImage: Boolean) {
        binding.apply {
            btnSend.toHide()
            progressBar.toShow()

            tvToolbarState.apply {
                text =
                    if (isImage) getString(R.string.sending_image) else getString(R.string.typing)
                toShow()
            }
        }

        val messageEntityObjectBox = MessageEntityObjectBox()
        messageEntityObjectBox.message =
            if (isImage) getString(R.string.sending_image) else getString(R.string.typing)
        messageEntityObjectBox.sentBy =
            if (isImage) MessageState.SENDING_IMAGE.name else MessageState.TYPING.name
        messageEntityObjectBox.time = PersianDate().getClockString()
        finalList.add(messageEntityObjectBox)

        if (isImage) {
            imageViewModel.generateImage(question.substringAfter(IMAGE).trim())
        } else {
            chatViewModel.chatMessageList.add(
                ChatPostBody.Message(
                    content = question,
                    role = ChatRole.USER.name.lowercase()
                )
            )
            chatViewModel.sendMessage()
        }
    }

    private fun initObserve() {
        chatViewModel.liveChatData.observe(viewLifecycleOwner) {
            addResponse(it.choices[0].message.content, false)
        }

        chatViewModel.liveError.observe(viewLifecycleOwner) {
            addResponse(getString(R.string.error_server), false)
        }

        imageViewModel.liveDataImage.observe(viewLifecycleOwner) { response ->
            response.data?.get(0)?.url?.let {
                addResponse(it, true)
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        isRestoredFromBackStack = true
    }

    private companion object {
        const val IMAGE = "/image"
    }

}