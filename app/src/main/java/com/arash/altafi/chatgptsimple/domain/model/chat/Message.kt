package com.arash.altafi.chatgptsimple.domain.model.chat

data class Message(
    var message: String,
    var sentBy: MessageState,
    var isImage: Boolean
)
