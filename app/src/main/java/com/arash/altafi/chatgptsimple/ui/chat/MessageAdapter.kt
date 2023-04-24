package com.arash.altafi.chatgptsimple.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.chatgptsimple.databinding.ChatItemBinding
import com.arash.altafi.chatgptsimple.domain.model.chat.Message
import com.arash.altafi.chatgptsimple.domain.model.chat.MessageState
import com.arash.altafi.chatgptsimple.ext.copyTextToClipboard
import com.arash.altafi.chatgptsimple.ext.shareContent
import com.arash.altafi.chatgptsimple.ext.toGone
import com.arash.altafi.chatgptsimple.ext.toShow
import com.bumptech.glide.Glide

class MessageAdapter(private var messageList: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context))
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = messageList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = messageList.size

    inner class MyViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Message) = binding.apply {
            when (item.sentBy) {
                MessageState.TYPING -> {
                    llRightChatView.toGone()
                    llLeftChatView.toShow()
                    tvCopy.toGone()
                    tvShare.toGone()
                    progressTyping.toShow()
                    ivImage.toGone()
                }
                MessageState.ME -> {
                    llLeftChatView.toGone()
                    tvCopy.toGone()
                    tvShare.toGone()
                    llRightChatView.toShow()
                    progressTyping.toGone()
                    ivImage.toGone()
                    tvRightChat.text = item.message
                }
                else -> {
                    llRightChatView.toGone()
                    progressTyping.toGone()
                    if (item.isImage) {
                        Glide.with(root.context).load(item.message).into(binding.ivImage)
                        ivImage.toShow()
                        llLeftChatView.toGone()
                        tvLeftChat.toGone()
                    } else {
                        tvCopy.toShow()
                        tvShare.toShow()
                        llLeftChatView.toShow()
                        tvLeftChat.toShow()
                        tvLeftChat.text = item.message
                        ivImage.toGone()
                    }
                }
            }

            tvCopy.setOnClickListener {
                it.context.copyTextToClipboard(tvLeftChat.text.toString())
            }

            tvShare.setOnClickListener {
                it.context.shareContent(tvLeftChat.text.toString())
            }
        }
    }
}
