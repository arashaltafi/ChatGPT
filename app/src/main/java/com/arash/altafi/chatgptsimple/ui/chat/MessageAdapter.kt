package com.arash.altafi.chatgptsimple.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.ItemImageRecevieBinding
import com.arash.altafi.chatgptsimple.databinding.ItemSendBinding
import com.arash.altafi.chatgptsimple.databinding.ItemTextRecevieBinding
import com.arash.altafi.chatgptsimple.domain.model.chat.MessageState
import com.arash.altafi.chatgptsimple.domain.provider.local.MessageEntityObjectBox
import com.arash.altafi.chatgptsimple.ext.copyTextToClipboard
import com.arash.altafi.chatgptsimple.ext.shareContent
import com.arash.altafi.chatgptsimple.ext.shareImage
import com.arash.altafi.chatgptsimple.ext.toGone
import com.arash.altafi.chatgptsimple.ext.toShow
import com.bumptech.glide.Glide

class MessageAdapter(private var messageList: ArrayList<MessageEntityObjectBox>) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
        private const val VIEW_TYPE_IMAGE_RECEIVED = 3
    }

    var onClickImageListener: ((String) -> Unit)? = null
    var onClickReplyListener: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> {
                MySendViewHolder(ItemSendBinding.inflate(LayoutInflater.from(parent.context)))
            }

            VIEW_TYPE_MESSAGE_RECEIVED -> {
                MyReceiveTextViewHolder(ItemTextRecevieBinding.inflate(LayoutInflater.from(parent.context)))
            }

            else -> {
                MyReceiveImageViewHolder(ItemImageRecevieBinding.inflate(LayoutInflater.from(parent.context)))
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = messageList[position]
        when (holder) {
            is MyReceiveTextViewHolder -> {
                holder.bind(item)
            }

            is MyReceiveImageViewHolder -> {
                holder.bind(item)
            }

            is MySendViewHolder -> {
                holder.bind(item)
            }
        }
    }

    override fun getItemCount(): Int = messageList.size

    override fun getItemViewType(position: Int): Int {
        return when (messageList[position].sentBy) {
            MessageState.ME.name -> VIEW_TYPE_MESSAGE_SENT
            MessageState.BOT_TEXT.name -> VIEW_TYPE_MESSAGE_RECEIVED
            MessageState.BOT_IMAGE.name -> VIEW_TYPE_IMAGE_RECEIVED
            MessageState.TYPING.name -> VIEW_TYPE_MESSAGE_RECEIVED
            MessageState.SENDING_IMAGE.name -> VIEW_TYPE_IMAGE_RECEIVED
            else -> -1
        }
    }

    inner class MyReceiveTextViewHolder(private val binding: ItemTextRecevieBinding) :
        ViewHolder(binding.root) {
        fun bind(item: MessageEntityObjectBox) = binding.apply {
            when (item.sentBy) {
                MessageState.BOT_TEXT.name -> {
                    tvTime.toShow()
                    ivCopy.toShow()
                    ivShare.toShow()
                    progressTyping.toGone()

                    //TODO
                    tvLeftChat.text = item.message
                }

                MessageState.TYPING.name -> {
                    tvTime.toGone()
                    ivCopy.toGone()
                    ivShare.toGone()
                    progressTyping.toShow()
                    tvLeftChat.text = ""
                }

                else -> {}
            }

            cvIn.setOnLongClickListener {
                ivCopy.performClick()
                true
            }

            tvTime.text = item.time

            ivCopy.setOnClickListener {
                it.context.copyTextToClipboard(item.message.toString())
            }

            ivShare.setOnClickListener {
                it.context.shareContent(item.message.toString())
            }
        }
    }

    inner class MyReceiveImageViewHolder(private val binding: ItemImageRecevieBinding) :
        ViewHolder(binding.root) {
        fun bind(item: MessageEntityObjectBox) = binding.apply {
            when (item.sentBy) {
                MessageState.BOT_IMAGE.name -> {
                    tvTime.toShow()
                    ivShare.toShow()
                    progressSendingImage.toGone()
                    Glide.with(root.context).load(item.message).into(ivImage)
                    ivImage.setOnClickListener {
                        onClickImageListener?.invoke(item.message.toString())
                    }
                }

                MessageState.SENDING_IMAGE.name -> {
                    tvTime.toGone()
                    ivShare.toGone()
                    progressSendingImage.toShow()
                    Glide.with(root.context).load(R.color.transparent).into(ivImage)
                }

                else -> {}
            }

            tvTime.text = item.time

            ivShare.setOnClickListener {
                it.context.shareImage(ivShare.drawable.toBitmap())
            }
        }
    }

    inner class MySendViewHolder(private val binding: ItemSendBinding) :
        ViewHolder(binding.root) {
        fun bind(item: MessageEntityObjectBox) = binding.apply {
            tvRightChat.text = item.message
            tvTime.text = item.time

            llRightChatView.setOnLongClickListener {
                ivCopy.performClick()
                true
            }

            ivCopy.setOnClickListener {
                it.context.copyTextToClipboard(item.message.toString())
            }

            ivEdit.setOnClickListener {
                onClickReplyListener?.invoke(item.message.toString())
            }
        }
    }
}
