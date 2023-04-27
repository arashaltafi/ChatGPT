package com.arash.altafi.chatgptsimple.domain.provider.local

import io.objectbox.annotation.ConflictStrategy
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique

@Entity
class DialogEntityObjectBox {
    @Id
    var id: Long? = null

    @Index
    @Unique(onConflict = ConflictStrategy.REPLACE)
    var dialogId: Long? = null
    var messageCount: Int? = null
    var message: String? = null
    var sentBy: String? = null
}