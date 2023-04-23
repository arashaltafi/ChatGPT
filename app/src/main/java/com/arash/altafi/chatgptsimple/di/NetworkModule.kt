package com.arash.altafi.chatgptsimple.di

import com.arash.altafi.chatgptsimple.BuildConfig
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideGSon(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    @Named("OpenAIURL")
    fun provideBaseURLCHAT(): String {
        return BuildConfig.OPENAI_URL
    }

    @CHATOpenAI
    @Singleton
    @Provides
    fun provideChatRetrofit(
        @CHATOpenAI okHttpClient: OkHttpClient, gSon: Gson,
        @Named("OpenAIURL") baseURL: String
    ): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(gSon))
            .client(okHttpClient)
            .build()
    }

    @IMAGEOpenAI
    @Singleton
    @Provides
    fun provideImageRetrofit(
        @IMAGEOpenAI okHttpClient: OkHttpClient, gSon: Gson,
        @Named("OpenAIURL") baseURL: String
    ): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create(gSon))
            .client(okHttpClient)
            .build()
    }

    @CHATOpenAI
    @Singleton
    @Provides
    fun provideChatOkHttp() = OkHttpClient.Builder()
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder().run {
                addHeader("Authorization", "Bearer ${BuildConfig.TOKEN}")
                build()
            }
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    @IMAGEOpenAI
    @Singleton
    @Provides
    fun provideImageOkHttp() = OkHttpClient.Builder()
        .writeTimeout(10, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder().run {
                addHeader("Authorization", "Bearer ${BuildConfig.TOKEN}")
                build()
            }
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

}