package com.arash.altafi.chatgptsimple.ui.image

import com.arash.altafi.chatgptsimple.base.BaseViewModel
import com.arash.altafi.chatgptsimple.domain.model.image.ImagePostBody
import com.arash.altafi.chatgptsimple.domain.model.image.ImageResponseBody
import com.arash.altafi.chatgptsimple.domain.repository.ImageRepository
import com.arash.altafi.chatgptsimple.utils.liveData.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ImageViewModel @Inject constructor(
    private val imageRepository: ImageRepository
) : BaseViewModel() {

    private val _liveDataImage = SingleLiveData<ImageResponseBody>()
    val liveDataImage: SingleLiveData<ImageResponseBody>
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