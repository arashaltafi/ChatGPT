package com.arash.altafi.chatgptsimple.ui.dialog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.chatgptsimple.data.local.DialogEntity
import com.arash.altafi.chatgptsimple.databinding.DialogItemBinding

class DialogAdapter(private var dialogList: ArrayList<DialogEntity>) :
    RecyclerView.Adapter<DialogAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DialogItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dialogList[position])
    }

    override fun getItemCount(): Int = dialogList.size

    inner class ViewHolder(private val binding: DialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dialogModel: DialogEntity) = binding.apply {
            tvTitle.text = dialogModel.title
            tvBadge.text = dialogModel.messageCount.toString()
        }
    }

}