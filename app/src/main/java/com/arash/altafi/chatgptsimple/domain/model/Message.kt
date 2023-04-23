package com.arash.altafi.chatgptsimple.domain.model

data class Message(
    var message: String,
    var sentBy: MessageState
)
