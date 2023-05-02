package com.arash.altafi.chatgptsimple.domain.provider.local

import android.content.Context
import io.objectbox.BoxStore

object ObjectBox {

    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore =
            MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .name("toDoObjectBox")
            .build()
    }

}