package com.arash.altafi.chatgptsimple.utils

import com.aaaamirabbas.reactor.handler.Reactor
import javax.inject.Inject

class Cache @Inject constructor(
    private val reactorAES: Reactor,
    private val reactorBase64: Reactor,
) {

    private val _tokenAES = "_tokenAES"
    private val _tokenBase64 = "_tokenBase64"

    var tokenAES: String
        get() = reactorAES.get(_tokenAES, "")
        set(value) {
            reactorAES.put(_tokenAES, value)
        }

    var tokenBase64: String
        get() = reactorBase64.get(_tokenBase64, "")
        set(value) {
            reactorBase64.put(_tokenBase64, value)
        }

    fun eraseAllData() {
        reactorAES.eraseAllData()
        reactorBase64.eraseAllData()
    }
}