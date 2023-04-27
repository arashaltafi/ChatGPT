package com.arash.altafi.chatgptsimple.ui.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.chatgptsimple.databinding.DialogItemBinding
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntityObjectBox

class DialogAdapter(private var dialogList: ArrayList<DialogEntityObjectBox>) :
    RecyclerView.Adapter<DialogAdapter.ViewHolder>() {

    var onLongClickListener: ((View, DialogEntityObjectBox) -> Unit)? = null
    var onClickListener: ((DialogEntityObjectBox) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DialogItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dialogList[position])
    }

    override fun getItemCount(): Int = dialogList.size

    fun getDialogEntity(position: Int): DialogEntityObjectBox = dialogList[position]

    inner class ViewHolder(private val binding: DialogItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dialogModel: DialogEntityObjectBox) = binding.apply {
            root.setOnLongClickListener {
                onLongClickListener?.invoke(it, dialogModel)
                true
            }

            root.setOnClickListener {
                onClickListener?.invoke(dialogModel)
            }

            tvTitle.text = dialogModel.message
            tvBadge.text = dialogModel.messageCount.toString()
        }
    }

}