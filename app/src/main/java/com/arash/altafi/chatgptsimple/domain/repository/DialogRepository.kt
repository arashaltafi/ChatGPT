package com.arash.altafi.chatgptsimple.domain.repository

import com.arash.altafi.chatgptsimple.base.BaseRepository
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntity
import com.arash.altafi.chatgptsimple.domain.provider.local.MessengerDao
import javax.inject.Inject

class DialogRepository @Inject constructor(
    private val messengerDao: MessengerDao
) : BaseRepository() {

    fun insert(dialogEntity: DialogEntity) {
        messengerDao.insertDialog(dialogEntity)
    }

    fun update(dialogEntity: DialogEntity) {
        messengerDao.updateDialog(dialogEntity)
    }

    fun delete(id: Long) {
        messengerDao.deleteDialogById(id)
    }

    fun delete(dialogEntity: DialogEntity) {
        messengerDao.deleteDialog(dialogEntity)
    }

    fun getAll(): List<DialogEntity> = messengerDao.getAllDialog()

}