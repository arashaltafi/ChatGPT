package com.arash.altafi.chatgptsimple.domain.provider.local

import io.objectbox.annotation.Backlink
import io.objectbox.annotation.ConflictStrategy
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
class DialogEntityObjectBox {
    @Id
    var id: Long? = null

    @Index
    @Unique(onConflict = ConflictStrategy.REPLACE)
    var dialogId: Long? = null
    var lastTime: Long? = null

    @Backlink(to = "dialog")
    lateinit var messages: ToMany<MessageEntityObjectBox>
}

@Entity
class MessageEntityObjectBox {
    @Id
    var id: Long? = null
    var message: String? = null
    var sentBy: String? = null
    var time: String? = null

    lateinit var dialog: ToOne<DialogEntityObjectBox>
}