package com.arash.altafi.chatgptsimple.ui.chat

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private val binding by lazy {
        FragmentChatBinding.inflate(layoutInflater)
    }

    private val args by navArgs<ChatFragmentArgs>()

    private val dialogViewModel: DialogViewModel by viewModels()
    private val chatViewModel: ChatViewModel by viewModels()
    private val imageViewModel: ImageViewModel by viewModels()

    private var finalList: ArrayList<Pair<String, String>> = arrayListOf()
    private var messageList: ArrayList<String> = arrayListOf()
    private var sentByList: ArrayList<String> = arrayListOf()
    private lateinit var messageAdapter: MessageAdapter

    private val welcomeMessage = "Hi, How may I assist you today?"
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
        windowInsetsHelper.isAutoResizeKeyboard = true
    }

    private fun init() = binding.apply {
        if (args.dialogId != -1L) {
            dialogViewModel.getDialogByIdObjectBox(args.dialogId)?.message?.forEach { message ->
                messageList.add(message)
            }
            dialogViewModel.getDialogByIdObjectBox(args.dialogId)?.sentBy?.forEach { sentBy ->
                sentByList.add(sentBy)
            }

            messageList.zip(sentByList).toCollection(finalList)

            dialogViewModel.getDialogByIdObjectBox(args.dialogId)
        } else {
            finalList.add(Pair(welcomeMessage, MessageState.BOT_TEXT.name))
            messageList.add(welcomeMessage)
            sentByList.add(MessageState.BOT_TEXT.name)

            val dialogEntity = DialogEntityObjectBox()
            dialogEntity.dialogId = getLastIdOfDB() + 1
            dialogEntity.message = messageList
            dialogEntity.sentBy = sentByList

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

        messageAdapter = MessageAdapter(finalList)
        rvChat.adapter = messageAdapter

        messageAdapter.onClickImageListener = {
            findNavController().navigate(
                ChatFragmentDirections.actionChatFragmentToImageFragment(
                    it
                )
            )
        }

        btnSend.setOnClickListener {
            if (checkNetWork()) {
                val question = edtMessage.text.toString().trim()
                edtMessage.setText("")
                if (question.isEmpty() || question.substringAfter(IMAGE).trim().isEmpty()) {
                    toast("Please Write Your Question")
                    edtMessage.error = "Please Write Your Question"
                } else {
                    it.hideKeyboard()
                    addToChat(question, MessageState.ME)
                    callAPI(question, question.startsWith(IMAGE))
                }
            } else {
                toast("Please Turn On Your Internet!!!")
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
            Gravity.BOTTOM.or(Gravity.END),
            setTint = false
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
        finalList.add(Pair(message, sentBy.name))
        sentByList.add(sentBy.name)
        messageList.add(message)

        messageAdapter.notifyDataSetChanged()
        binding.rvChat.smoothScrollToPosition(messageAdapter.itemCount)

        if (sentBy != MessageState.TYPING && sentBy != MessageState.SENDING_IMAGE) {
            val dialogEntity = DialogEntityObjectBox()
            dialogEntity.dialogId = getLastIdOfDB()
            dialogEntity.message = messageList
            dialogEntity.sentBy = sentByList
            dialogViewModel.updateDialogObjectBox(dialogEntity)
        }
    }

    private fun addResponse(response: String, isImage: Boolean) = binding.apply {
        finalList.removeAt(finalList.size - 1)
        messageList.removeAt(messageList.size - 1)
        sentByList.removeAt(sentByList.size - 1)
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
                text = if (isImage) "Sending Image... " else "Typing... "
                toShow()
            }
        }

        finalList.add(
            Pair(
                if (isImage) "Sending Image... " else "Typing... ",
                if (isImage) MessageState.SENDING_IMAGE.name else MessageState.TYPING.name
            )
        )

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
            addResponse("There is Was Error Please Send Again Later ...", false)
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

    private companion object {
        const val IMAGE = "/image"
    }

}