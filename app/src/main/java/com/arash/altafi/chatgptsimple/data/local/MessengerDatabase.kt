package com.arash.altafi.chatgptsimple.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DialogEntity::class], version = 1)
abstract class MessengerDatabase : RoomDatabase() {

    companion object {
        var messengerDatabase: MessengerDatabase? = null

        fun getAppDataBase(context: Context): MessengerDatabase? {
            if (messengerDatabase == null) {
                synchronized(MessengerDatabase::class) {
                    messengerDatabase =
                        Room
                            .databaseBuilder(context, MessengerDatabase::class.java, "db_chat_gpt")
                            .allowMainThreadQueries()
                            .build()
                }
            }
            return messengerDatabase
        }
    }

    abstract fun MessengerDao(): MessengerDao

}