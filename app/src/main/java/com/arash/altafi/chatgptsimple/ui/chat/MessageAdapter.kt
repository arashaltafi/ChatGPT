package com.arash.altafi.chatgptsimple.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.ItemImageRecevieBinding
import com.arash.altafi.chatgptsimple.databinding.ItemSendBinding
import com.arash.altafi.chatgptsimple.databinding.ItemTextRecevieBinding
import com.arash.altafi.chatgptsimple.domain.model.chat.Message
import com.arash.altafi.chatgptsimple.domain.model.chat.MessageState
import com.arash.altafi.chatgptsimple.ext.copyTextToClipboard
import com.arash.altafi.chatgptsimple.ext.shareContent
import com.arash.altafi.chatgptsimple.ext.toGone
import com.arash.altafi.chatgptsimple.ext.toShow
import com.bumptech.glide.Glide

class MessageAdapter(private var messageList: ArrayList<Message>) :
    RecyclerView.Adapter<ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
        private const val VIEW_TYPE_IMAGE_RECEIVED = 3
    }

    var onClickImageListener: ((String) -> Unit)? = null

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
            MessageState.ME -> VIEW_TYPE_MESSAGE_SENT
            MessageState.BOT_TEXT -> VIEW_TYPE_MESSAGE_RECEIVED
            MessageState.BOT_IMAGE -> VIEW_TYPE_IMAGE_RECEIVED
            MessageState.TYPING -> VIEW_TYPE_MESSAGE_RECEIVED
            MessageState.SENDING_IMAGE -> VIEW_TYPE_IMAGE_RECEIVED
        }
    }

    inner class MyReceiveTextViewHolder(private val binding: ItemTextRecevieBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Message) = binding.apply {
            when (item.sentBy) {
                MessageState.BOT_TEXT -> {
                    tvCopy.toShow()
                    tvShare.toShow()
                    progressTyping.toGone()
                    tvLeftChat.text = item.message
                }

                MessageState.TYPING -> {
                    tvCopy.toGone()
                    tvShare.toGone()
                    progressTyping.toShow()
                    tvLeftChat.text = ""
                }

                else -> {}
            }

            tvCopy.setOnClickListener {
                it.context.copyTextToClipboard(tvLeftChat.text.toString())
            }

            tvShare.setOnClickListener {
                it.context.shareContent(tvLeftChat.text.toString())
            }
        }
    }

    inner class MyReceiveImageViewHolder(private val binding: ItemImageRecevieBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Message) = binding.apply {
            when (item.sentBy) {
                MessageState.BOT_IMAGE -> {
                    progressSendingImage.toGone()
                    Glide.with(root.context).load(item.message).into(ivImage)
                    ivImage.setOnClickListener {
                        onClickImageListener?.invoke(item.message)
                    }
                }

                MessageState.SENDING_IMAGE -> {
                    progressSendingImage.toShow()
                    Glide.with(root.context).load(R.color.transparent).into(ivImage)
                }

                else -> {}
            }
        }
    }

    inner class MySendViewHolder(private val binding: ItemSendBinding) :
        ViewHolder(binding.root) {
        fun bind(item: Message) = binding.apply {
            tvRightChat.text = item.message
        }
    }
}
