package com.arash.altafi.chatgptsimple.domain.provider.remote

import com.arash.altafi.chatgptsimple.base.BaseService
import com.arash.altafi.chatgptsimple.domain.model.ChatPostBody
import com.arash.altafi.chatgptsimple.domain.model.ChatResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatService : BaseService {

    @POST("v1/chat/completions")
    suspend fun sendMessage(
        @Body chatPostBody: ChatPostBody
    ): Response<ChatResponseBody>

}