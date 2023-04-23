package com.arash.altafi.chatgptsimple.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object RepositoryModule {

//    @ViewModelScoped
//    @Provides
//    fun provideSalaryRepository(
//        service: ChatService,
//    ) = ChatRepository(service)

}