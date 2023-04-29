package com.arash.altafi.chatgptsimple.ui.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.chatgptsimple.R
import com.arash.altafi.chatgptsimple.databinding.DialogItemBinding
import com.arash.altafi.chatgptsimple.domain.model.chat.MessageState
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntityObjectBox
import com.arash.altafi.chatgptsimple.ext.getDateClassifiedByDayMothYear
import saman.zamani.persiandate.PersianDate

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

            tvTitle.text = if (dialogModel.sentBy?.lastOrNull() == MessageState.BOT_IMAGE.name)
                root.context.getString(R.string.image)
            else
                dialogModel.message?.lastOrNull().toString()
            tvBadge.text = (dialogModel.message?.count() ?: 0).toString()

            val time = dialogModel.lastTime ?: System.currentTimeMillis()
            tvTime.text = PersianDate(time).getDateClassifiedByDayMothYear()

        }
    }

}