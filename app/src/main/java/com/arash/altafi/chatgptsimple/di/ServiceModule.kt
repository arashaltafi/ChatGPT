package com.arash.altafi.chatgptsimple.di

import com.arash.altafi.chatgptsimple.domain.provider.remote.ChatService
import com.arash.altafi.chatgptsimple.domain.provider.remote.ImageService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import retrofit2.Retrofit

@Module
@InstallIn(ViewModelComponent::class)
object ServiceModule {

    @ViewModelScoped
    @Provides
    fun provideChatService(@CHATOpenAI retrofit: Retrofit): ChatService =
        retrofit.create(ChatService::class.java)

    @ViewModelScoped
    @Provides
    fun provideImageService(@IMAGEOpenAI retrofit: Retrofit): ImageService =
        retrofit.create(ImageService::class.java)

}