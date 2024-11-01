package com.example.baddit.domain.usecases

import com.example.baddit.domain.repository.LocalThemeManager
import kotlinx.coroutines.flow.Flow

class ReadDarkTheme(private val localThemeManager: LocalThemeManager) {
    suspend operator fun invoke(): Flow<Boolean> {
        return localThemeManager.readDarkTheme();
    }
}