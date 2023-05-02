package com.arash.altafi.chatgptsimple.domain.repository

import com.arash.altafi.chatgptsimple.base.BaseRepository
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntityObjectBox
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntityObjectBox_
import com.arash.altafi.chatgptsimple.domain.provider.local.ObjectBox.boxStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class DialogRepository @Inject constructor() : BaseRepository() {

    private val coroutineContext: CoroutineContext get() = Dispatchers.Main
    private val dialogBox = boxStore.boxFor(DialogEntityObjectBox::class.java)

    fun saveDialogObjectBox(dialogEntity: DialogEntityObjectBox) =
        CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
            dialogBox.put(dialogEntity)
        }

    fun updateDialogObjectBox(dialogEntity: DialogEntityObjectBox) =
        CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
            dialogBox.put(dialogEntity)
        }

    fun deleteDialogByIdObjectBox(id: Long) {
        dialogBox.remove(id)
    }

    fun deleteDialogObjectBox(dialogEntity: DialogEntityObjectBox) =
        CoroutineScope(coroutineContext).launch(Dispatchers.IO) {
            dialogBox.remove(dialogEntity)
        }

    fun getDialogByIdObjectBox(id: Long): DialogEntityObjectBox? = dialogBox.query()
        .equal(DialogEntityObjectBox_.id, id)
        .build()
        .findFirst()

    fun getLastDialogIdObjectBox() = dialogBox.query()
        .orderDesc(DialogEntityObjectBox_.dialogId)
        .build()
        .findFirst()?.dialogId ?: 0L

    fun getAllDialogObjectBox(): List<DialogEntityObjectBox> {
        val usersQuery = dialogBox.query()
            .orderDesc(DialogEntityObjectBox_.id)
            .build()
        return usersQuery.use { it.find() }
    }

    fun getDialogId(dialogEntity: DialogEntityObjectBox) = dialogBox.put(dialogEntity)
}