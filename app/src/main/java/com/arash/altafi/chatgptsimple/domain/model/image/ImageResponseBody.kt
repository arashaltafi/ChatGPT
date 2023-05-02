package com.arash.altafi.chatgptsimple.domain.model.image

import com.google.gson.annotations.SerializedName

data class ImageResponseBody(
    @SerializedName("data")
    val data: List<Media>? = null
)

data class Media(
    @SerializedName("url")
    val url: String,
)