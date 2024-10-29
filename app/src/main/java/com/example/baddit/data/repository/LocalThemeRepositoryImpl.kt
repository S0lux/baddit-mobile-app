package com.example.baddit.data.repository
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeRepository(private val dataStore: DataStore<Preferences>) {
    private companion object {
        val IS_DARK_THEME = booleanPreferencesKey("darkTheme")
    }

    val isDarkTheme: Flow<Boolean> =
        dataStore.data.map { preferences ->
            preferences[IS_DARK_THEME] ?: false
        }

    suspend fun setDarkTheme(darkTheme: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_DARK_THEME] = darkTheme
        }
    }


}