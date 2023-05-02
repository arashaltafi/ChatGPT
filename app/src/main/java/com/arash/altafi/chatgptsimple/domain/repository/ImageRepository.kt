package com.arash.altafi.chatgptsimple.domain.repository

import com.arash.altafi.chatgptsimple.base.BaseRepository
import com.arash.altafi.chatgptsimple.domain.model.image.ImagePostBody
import com.arash.altafi.chatgptsimple.domain.provider.remote.ImageService
import javax.inject.Inject

class ImageRepository @Inject constructor(
    private val service: ImageService
) : BaseRepository() {

    fun generateImage(
        imagePostBody: ImagePostBody
    ) = callApi {
        service.generateImage(imagePostBody)
    }

}