package com.example.baddit.di

import android.content.Context
import com.example.baddit.data.remote.BadditAPI
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideAPI(@ApplicationContext appContext: Context): BadditAPI {
        val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(appContext))

        val logInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client: OkHttpClient = OkHttpClient.Builder().apply {
            addInterceptor(logInterceptor)
            cookieJar(cookieJar)
        }.build()

        return Retrofit.Builder()
            .baseUrl("https://api.baddit.life/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(BadditAPI::class.java)
    }

}