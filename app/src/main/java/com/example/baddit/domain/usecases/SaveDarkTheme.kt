package com.example.baddit.domain.usecases

import com.example.baddit.domain.repository.LocalThemeManager

class SaveDarkTheme(private val localThemeManager: LocalThemeManager) {
    suspend operator fun invoke(b: Boolean) {
        localThemeManager.saveDarkTheme(b);
    }
}