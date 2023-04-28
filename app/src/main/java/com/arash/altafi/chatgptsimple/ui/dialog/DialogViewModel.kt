package com.arash.altafi.chatgptsimple.ui.dialog

import com.arash.altafi.chatgptsimple.base.BaseViewModel
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntity
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntityObjectBox
import com.arash.altafi.chatgptsimple.domain.repository.DialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    private val dialogRepository: DialogRepository
) : BaseViewModel() {

    //Room
    fun saveDialog(dialogEntity: DialogEntity) {
        dialogRepository.saveDialog(dialogEntity)
    }

    fun updateDialog(dialogEntity: DialogEntity) {
        dialogRepository.updateDialog(dialogEntity)
    }

    fun deleteDialogById(id: Long) {
        dialogRepository.deleteDialogById(id)
    }

    fun deleteDialog(dialogEntity: DialogEntity) {
        dialogRepository.deleteDialog(dialogEntity)
    }

    fun getDialogById(id: Long): DialogEntity = dialogRepository.getDialogById(id)

    fun getLastDialogId(): Long = dialogRepository.getLastDialogId()

    fun getAllDialog(): List<DialogEntity> = dialogRepository.getAllDialog()

    //Object Box
    fun saveDialogObjectBox(dialogEntity: DialogEntityObjectBox) {
        dialogRepository.saveDialogObjectBox(dialogEntity)
    }

    fun updateDialogObjectBox(dialogEntity: DialogEntityObjectBox) {
        dialogRepository.updateDialogObjectBox(dialogEntity)
    }

    fun deleteDialogByIdObjectBox(id: Long) {
        dialogRepository.deleteDialogByIdObjectBox(id)
    }

    fun deleteDialogObjectBox(dialogEntity: DialogEntityObjectBox) {
        dialogRepository.deleteDialogObjectBox(dialogEntity)
    }

    fun getDialogByIdObjectBox(id: Long): DialogEntityObjectBox? =
        dialogRepository.getDialogByIdObjectBox(id)

    fun getLastDialogIdObjectBox(): Long = dialogRepository.getLastDialogIdObjectBox()

    fun getAllDialogObjectBox(): List<DialogEntityObjectBox> =
        dialogRepository.getAllDialogObjectBox()

}