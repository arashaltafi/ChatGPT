package com.arash.altafi.chatgptsimple.utils

import android.content.Context
import android.net.ConnectivityManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.net.InetSocketAddress
import java.net.Socket

object NetworkUtils {

    fun isConnected(context: Context): Boolean {
        val connectivityMgr =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE)
                    as ConnectivityManager
        val networkInfo = connectivityMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    suspend fun hasOnline(serverURL: String) = flow {
        while (true) {
            try {
                val timeoutMs = 1500
                val socket = Socket()
                val socketAddress = InetSocketAddress(serverURL, 80)

                socket.connect(socketAddress, timeoutMs)
                socket.close()
                emit(true)
            } catch (e: Exception) {
                emit(false)
            }

            delay(1000)
        }
    }.flowOn(Dispatchers.Default)
}