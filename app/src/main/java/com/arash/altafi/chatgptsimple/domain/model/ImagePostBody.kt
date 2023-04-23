package com.arash.altafi.chatgptsimple.domain.model

import com.google.gson.annotations.SerializedName

data class ImagePostBody(
    @SerializedName("model")
    val model: String,

    @SerializedName("prompt")
    val prompt: String,

    @SerializedName("num_images")
    val numImages: Int,

    @SerializedName("size")
    val size: String,

    @SerializedName("response_format")
    val responseFormat: String
)