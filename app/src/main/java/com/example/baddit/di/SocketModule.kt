package com.example.baddit.di

import com.example.baddit.data.socket.SocketManager
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.ChatRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SocketModule {
    @Provides
    @Singleton
    fun provideSocketManager(
        chatRepository: ChatRepository
    ): SocketManager {
        return SocketManager(chatRepository)
    }
}