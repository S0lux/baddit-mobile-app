package com.example.baddit.domain.repository

import kotlinx.coroutines.flow.Flow

interface LocalThemeManager {
    suspend fun saveDarkTheme(boolean: Boolean)

    fun readDarkTheme(): Flow<Boolean>
}