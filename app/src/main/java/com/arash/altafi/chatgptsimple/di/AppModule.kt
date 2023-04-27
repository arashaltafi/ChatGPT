package com.arash.altafi.chatgptsimple.di

import android.app.Application
import android.content.Context
import com.aaaamirabbas.reactor.handler.Reactor
import com.arash.altafi.chatgptsimple.utils.Cache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Singleton
    @Provides
    @Named("AES")
    fun provideReactorAES(context: Context) = Reactor(context, true)

    @Singleton
    @Provides
    @Named("Base64")
    fun provideReactorBase64(context: Context) = Reactor(context, false)

    @Singleton
    @Provides
    fun provideAppCache(
        @Named("AES") reactorAES: Reactor,
        @Named("Base64") reactorBase64: Reactor,
    ) = Cache(reactorAES, reactorBase64)
}