package com.example.baddit.data.repository
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.baddit.data.utils.Constants
import com.example.baddit.data.utils.Constants.USER_SETTING
import com.example.baddit.domain.repository.LocalThemeManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalThemeRepositoryImpl( private val context: Context): LocalThemeManager {

    override suspend fun saveDarkTheme(darkTheme: String) {
        context.dataStore.edit{ settings ->
            settings[PreferencesKeys.DARK_THEME] = darkTheme;
        }
    }

    override fun readDarkTheme(): Flow<String> {
        return context.dataStore.data.map{
            preferences -> preferences[PreferencesKeys.DARK_THEME] ?: "System"
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_SETTING);

private object PreferencesKeys{
    val DARK_THEME = stringPreferencesKey(name = Constants.DARK_THEME)
}