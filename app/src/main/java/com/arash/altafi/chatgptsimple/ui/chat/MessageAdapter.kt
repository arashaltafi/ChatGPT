package com.arash.altafi.chatgptsimple.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.ItemImageRecevieBinding
import com.arash.altafi.chatgptsimple.databinding.ItemSendBinding
import com.arash.altafi.chatgptsimple.databinding.ItemTextRecevieBinding
import com.arash.altafi.chatgptsimple.domain.model.chat.MessageState
import com.arash.altafi.chatgptsimple.domain.provider.local.MessageEntityObjectBox
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
//                    tvCopy.toShow()
//                    tvShare.toShow()
                    progressTyping.toGone()
                    tvLeftChat.setText(item.message, true)
                }

                MessageState.TYPING.name -> {
                    tvTime.toGone()
//                    tvCopy.toGone()
//                    tvShare.toGone()
                    progressTyping.toShow()
                    tvLeftChat.text = ""
                }

                else -> {}
            }

            tvTime.text = item.time

            /*tvCopy.setOnClickListener {
                it.context.copyTextToClipboard(tvLeftChat.text.toString())
            }

            tvShare.setOnClickListener {
                it.context.shareContent(tvLeftChat.text.toString())
            }*/
        }
    }

    inner class MyReceiveImageViewHolder(private val binding: ItemImageRecevieBinding) :
        ViewHolder(binding.root) {
        fun bind(item: MessageEntityObjectBox) = binding.apply {
            when (item.sentBy) {
                MessageState.BOT_IMAGE.name -> {
                    tvTime.toShow()
                    progressSendingImage.toGone()
                    Glide.with(root.context).load(item.message).into(ivImage)
                    ivImage.setOnClickListener {
                        onClickImageListener?.invoke(item.message.toString())
                    }
                }

                MessageState.SENDING_IMAGE.name -> {
                    tvTime.toGone()
                    progressSendingImage.toShow()
                    Glide.with(root.context).load(R.color.transparent).into(ivImage)
                }

                else -> {}
            }

            tvTime.text = item.time
        }
    }

    inner class MySendViewHolder(private val binding: ItemSendBinding) :
        ViewHolder(binding.root) {
        fun bind(item: MessageEntityObjectBox) = binding.apply {
            tvRightChat.text = item.message
            tvTime.text = item.time
        }
    }
}
