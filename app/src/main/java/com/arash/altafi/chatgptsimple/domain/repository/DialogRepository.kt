package com.arash.altafi.chatgptsimple.domain.repository

import com.arash.altafi.chatgptsimple.base.BaseRepository
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntity
import com.arash.altafi.chatgptsimple.domain.provider.local.MessengerDao
import javax.inject.Inject

class DialogRepository @Inject constructor(
    private val messengerDao: MessengerDao
) : BaseRepository() {

    fun insertDialog(dialogEntity: DialogEntity) {
        messengerDao.insertDialog(dialogEntity)
    }

    fun updateDialog(dialogEntity: DialogEntity) {
        messengerDao.updateDialog(dialogEntity)
    }

    fun deleteDialogById(id: Long) {
        messengerDao.deleteDialogById(id)
    }

    fun deleteDialog(dialogEntity: DialogEntity) {
        messengerDao.deleteDialog(dialogEntity)
    }

    fun getDialogById(id: Long) = messengerDao.getDialogById(id)

    fun getLastDialogId() = messengerDao.getLastDialogId()

    fun getAllDialog(): List<DialogEntity> = messengerDao.getAllDialog()

}