package com.arash.altafi.chatgptsimple.base

import com.arash.altafi.chatgptsimple.ext.flowIO
import retrofit2.Response

abstract class BaseRepository {

    fun <T> callApi(networkCall: suspend () -> Response<T>) = flowIO {
        val response = networkCall()
        emit(response)
    }

}