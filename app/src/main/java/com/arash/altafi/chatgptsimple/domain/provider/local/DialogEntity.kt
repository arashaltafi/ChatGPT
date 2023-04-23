package com.arash.altafi.chatgptsimple.domain.provider.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arash.altafi.chatgptsimple.domain.model.MessageState

@Entity(tableName = "tbl_dialog")
class DialogEntity {
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null

    @ColumnInfo(name = "message_count")
    var messageCount: Int? = null

    @ColumnInfo(name = "message")
    var message: String? = null

    @ColumnInfo(name = "sent_by")
    var sentBy: MessageState? = null
}