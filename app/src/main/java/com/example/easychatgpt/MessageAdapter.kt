package com.example.easychatgpt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.easychatgpt.databinding.ChatItemBinding

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
        fun bind(message1: String, sentBy: MessageState) = binding.apply {
            if (sentBy == MessageState.ME) {
                llLeftChatView.visibility = View.GONE
                llRightChatView.visibility = View.VISIBLE
                tvRightChat.text = message1
            } else {
                llRightChatView.visibility = View.GONE
                llLeftChatView.visibility = View.VISIBLE
                tvLeftChat.text = message1
            }
        }
    }
}
