package com.arash.altafi.chatgptsimple.domain.provider.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, entities = [DialogEntity::class], exportSchema = false)
abstract class MessengerDatabase : RoomDatabase() {

    abstract fun MessengerDao(): MessengerDao

}