package com.example.baddit.di

import com.example.baddit.data.remote.PostAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    private val interceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client: OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(interceptor)
    }.build()

    @Provides
    @Singleton
    fun provideAPI(): PostAPI {
        return Retrofit.Builder()
            .baseUrl("https://api.baddit.life/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PostAPI::class.java)
    }

}