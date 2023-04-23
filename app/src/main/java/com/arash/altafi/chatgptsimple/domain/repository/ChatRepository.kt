package com.arash.altafi.chatgptsimple.domain.repository

import com.arash.altafi.chatgptsimple.base.BaseRepository
import com.arash.altafi.chatgptsimple.domain.model.ChatPostBody
import com.arash.altafi.chatgptsimple.domain.provider.remote.ChatService
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val service: ChatService
) : BaseRepository() {

    fun sendMessage(
        chatPostBody: ChatPostBody
    ) = callApi {
        service.sendMessage(chatPostBody)
    }

}