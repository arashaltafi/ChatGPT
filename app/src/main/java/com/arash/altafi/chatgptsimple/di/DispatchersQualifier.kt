package com.arash.altafi.chatgptsimple.di

import javax.inject.Qualifier

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoScope

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainScope

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainImmediateScope