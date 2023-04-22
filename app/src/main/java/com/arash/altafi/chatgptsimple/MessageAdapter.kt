package com.arash.altafi.chatgptsimple

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.chatgptsimple.databinding.ChatItemBinding

class MessageAdapter(private var messageList: ArrayList<Message>) :
    RecyclerView.Adapter<MessageAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ChatItemBinding.inflate(LayoutInflater.from(parent.context))
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val (message1, sentBy) = messageList[position]
        holder.bind(message1, sentBy)
    }

    override fun getItemCount(): Int = messageList.size

    inner class MyViewHolder(private val binding: ChatItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: String, sentBy: MessageState) = binding.apply {
            if (sentBy == MessageState.ME) {
                llLeftChatView.toGone()
                llRightChatView.toShow()
                tvRightChat.text = message
            } else {
                llRightChatView.toGone()
                llLeftChatView.toShow()
                tvLeftChat.text = message
            }
        }
    }
}
