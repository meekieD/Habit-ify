package com.dyusov.core.designsystem.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.dyusov.core.designsystem.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "habitify_theme")

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ThemeRepository {

    private val themeModeKey = stringPreferencesKey("theme_mode")

    override val themeMode: Flow<ThemeMode> = context.themeDataStore.data.map { preferences ->
        val themeName = preferences[themeModeKey] ?: ThemeMode.SYSTEM.name
        ThemeMode.entries.firstOrNull { it.name == themeName } ?: ThemeMode.SYSTEM
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        context.themeDataStore.edit { preferences ->
            preferences[themeModeKey] = mode.name
        }
    }
}