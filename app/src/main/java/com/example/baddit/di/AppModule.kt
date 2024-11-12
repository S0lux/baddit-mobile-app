package com.example.baddit.di

import android.app.Application
import android.content.Context
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.repository.LocalThemeRepositoryImpl
import com.example.baddit.domain.repository.LocalThemeManager
import com.example.baddit.domain.usecases.LocalThemeUseCases
import com.example.baddit.domain.usecases.ReadDarkTheme
import com.example.baddit.domain.usecases.SaveDarkTheme
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

    @Provides
    @Singleton
    fun provideLocalThemeManager(application: Application):LocalThemeManager = LocalThemeRepositoryImpl(application)

    @Provides
    @Singleton
    fun provideAppEntryUseCases(
        localThemeManager: LocalThemeManager
    ) = LocalThemeUseCases(
        readDarkTheme = ReadDarkTheme(localThemeManager),
        saveDarkTheme = SaveDarkTheme(localThemeManager)
    )

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader {
        return ImageLoader.Builder(context).components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            }
            else {
                add(GifDecoder.Factory())
            }
        }.build()
    }
}