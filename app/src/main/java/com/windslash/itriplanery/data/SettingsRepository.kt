package com.windslash.itriplanery.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Single DataStore instance for app-wide preferences.
private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

/**
 * Lightweight, reactive user preferences (survive restarts).
 * Currently: dark mode + whether the gamified "rank" UI is shown.
 */
class SettingsRepository(private val context: Context) {

    private object Keys {
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val GAMIFICATION = booleanPreferencesKey("gamification_enabled")
    }

    val darkMode: Flow<Boolean> = context.settingsDataStore.data
        .map { it[Keys.DARK_MODE] ?: false }

    val gamificationEnabled: Flow<Boolean> = context.settingsDataStore.data
        .map { it[Keys.GAMIFICATION] ?: true } // on by default — it's a charming feature

    suspend fun setDarkMode(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.DARK_MODE] = enabled }
    }

    suspend fun setGamificationEnabled(enabled: Boolean) {
        context.settingsDataStore.edit { it[Keys.GAMIFICATION] = enabled }
    }
}
