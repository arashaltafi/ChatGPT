package com.arash.altafi.chatgptsimple.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.chatgptsimple.databinding.ChatItemBinding
import com.arash.altafi.chatgptsimple.domain.model.Message
import com.arash.altafi.chatgptsimple.domain.model.MessageState
import com.arash.altafi.chatgptsimple.ext.toGone
import com.arash.altafi.chatgptsimple.ext.toShow

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
                    progressTyping.toShow()
                }
                MessageState.ME -> {
                    llLeftChatView.toGone()
                    llRightChatView.toShow()
                    progressTyping.toGone()
                    tvRightChat.text = item.message
                }
                else -> {
                    llRightChatView.toGone()
                    llLeftChatView.toShow()
                    progressTyping.toGone()
                    tvLeftChat.text = item.message
                }
            }
        }
    }
}
