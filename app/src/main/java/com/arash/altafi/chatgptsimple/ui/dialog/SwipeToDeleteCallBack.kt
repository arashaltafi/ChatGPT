package com.arash.altafi.chatgptsimple.ui.dialog

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntityObjectBox
import com.arash.altafi.chatgptsimple.ext.runAfter

abstract class SwipeToDeleteCallbackObjectBox(
    private val dialogViewModel: DialogViewModel,
    private val adapter: DialogAdapter,
    private val afterDeleted: (List<DialogEntityObjectBox>) -> Unit
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        dialogViewModel.deleteDialogObjectBox(adapter.getDialogEntity(viewHolder.adapterPosition))
        runAfter(200, {
            afterDeleted.invoke(dialogViewModel.getAllDialogObjectBox())
        })
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

}