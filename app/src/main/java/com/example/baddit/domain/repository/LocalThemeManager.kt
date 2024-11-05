package com.example.baddit.domain.repository

import kotlinx.coroutines.flow.Flow

interface LocalThemeManager {
    suspend fun saveDarkTheme(darkTheme: String)

    fun readDarkTheme(): Flow<String>
}