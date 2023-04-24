package com.arash.altafi.chatgptsimple.ui.image

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.arash.altafi.chatgptsimple.base.BaseViewModel
import com.arash.altafi.chatgptsimple.domain.model.image.ImagePostBody
import com.arash.altafi.chatgptsimple.domain.model.image.ImageResponseBody
import com.arash.altafi.chatgptsimple.domain.repository.ImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : BaseViewModel() {

    private val _liveDataImage = MutableLiveData<ImageResponseBody>()
    val liveDataImage: LiveData<ImageResponseBody>
        get() = _liveDataImage

    fun generateImage(text: String) = callApi(
        imageRepository.generateImage(
            ImagePostBody(
                "image-alpha-001", text, 1, "256x256", "url"
            )
        ),
        _liveDataImage
    )

}