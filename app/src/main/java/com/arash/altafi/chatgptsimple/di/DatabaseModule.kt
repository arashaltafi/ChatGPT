package com.arash.altafi.chatgptsimple.di

import android.content.Context
import androidx.room.Room
import com.arash.altafi.chatgptsimple.domain.provider.local.MessengerDao
import com.arash.altafi.chatgptsimple.domain.provider.local.MessengerDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun getRoomDatabase(@ApplicationContext context: Context): MessengerDatabase {
        return Room.databaseBuilder(context, MessengerDatabase::class.java, "db_chat_gpt")
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Singleton
    @Provides
    fun getUserDao(messengerDatabase: MessengerDatabase): MessengerDao {
        return messengerDatabase.MessengerDao()
    }
}