package com.arash.altafi.chatgptsimple.ui.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arash.altafi.chatgptsimple.base.BaseViewModel
import com.arash.altafi.chatgptsimple.domain.model.ImageResponseBody
import com.arash.altafi.chatgptsimple.domain.provider.local.DialogEntity
import com.arash.altafi.chatgptsimple.domain.repository.DialogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DialogViewModel @Inject constructor(
    private val dialogRepository: DialogRepository
) : BaseViewModel() {

    private val _liveDataImage = MutableLiveData<ImageResponseBody>()
    val liveDataImage: LiveData<ImageResponseBody>
        get() = _liveDataImage

    fun generateImage() {
        dialogRepository.getAll()
    }

    fun insert(dialogEntity: DialogEntity) {
        dialogRepository.insert(dialogEntity)
    }

    fun update(dialogEntity: DialogEntity) {
        dialogRepository.update(dialogEntity)
    }

    fun deleteById(id: Long) {
        dialogRepository.delete(id)
    }

    fun delete(dialogEntity: DialogEntity) {
        dialogRepository.delete(dialogEntity)
    }

    fun getAll(): List<DialogEntity> = dialogRepository.getAll()

}