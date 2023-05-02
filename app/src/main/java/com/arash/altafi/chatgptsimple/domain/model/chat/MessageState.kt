package com.arash.altafi.chatgptsimple.domain.model.chat

enum class MessageState {
    ME,
    BOT_TEXT,
    BOT_IMAGE,
    TYPING,
    SENDING_IMAGE
}