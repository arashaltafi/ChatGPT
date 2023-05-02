package com.arash.altafi.chatgptsimple.domain.model.chat

import com.google.gson.annotations.SerializedName

data class ChatPostBody(
    @SerializedName("frequency_penalty")
    val frequencyPenalty: Double,
    @SerializedName("max_tokens")
    val maxTokens: Int,
    @SerializedName("messages")
    val messages: List<Message>,
    @SerializedName("model")
    val model: String,
    @SerializedName("presence_penalty")
    val presencePenalty: Double,
    @SerializedName("temperature")
    val temperature: Double,
    @SerializedName("top_p")
    val topP: Int
) {
    data class Message(
        @SerializedName("content")
        val content: String,
        @SerializedName("role")
        val role: String
    )
}