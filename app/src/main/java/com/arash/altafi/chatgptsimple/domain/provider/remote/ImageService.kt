package com.arash.altafi.chatgptsimple.domain.provider.remote

import com.arash.altafi.chatgptsimple.base.BaseService
import com.arash.altafi.chatgptsimple.domain.model.image.ImagePostBody
import com.arash.altafi.chatgptsimple.domain.model.image.ImageResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ImageService : BaseService {

    @POST("v1/images/generations")
    suspend fun generateImage(
        @Body chatPostBody: ImagePostBody
    ): Response<ImageResponseBody>

}