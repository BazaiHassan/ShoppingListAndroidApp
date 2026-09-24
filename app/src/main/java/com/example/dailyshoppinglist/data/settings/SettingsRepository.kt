package com.example.dailyshoppinglist.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class ThemeMode { SYSTEM, LIGHT, DARK }

/** Apple system colors (light / dark variants). */
enum class AccentColor(val light: Long, val dark: Long) {
    BLUE(0xFF007AFF, 0xFF0A84FF),
    INDIGO(0xFF5856D6, 0xFF5E5CE6),
    PURPLE(0xFFAF52DE, 0xFFBF5AF2),
    PINK(0xFFFF2D55, 0xFFFF375F),
    RED(0xFFFF3B30, 0xFFFF453A),
    ORANGE(0xFFFF9500, 0xFFFF9F0A),
    GREEN(0xFF34C759, 0xFF30D158),
    TEAL(0xFF30B0C7, 0xFF40C8E0),
}

data class AppSettings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val accent: AccentColor = AccentColor.BLUE,
    val haptics: Boolean = true,
    val groupByCategory: Boolean = false,
)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(context: Context) {
    private val store = context.applicationContext.dataStore

    private object Keys {
        val THEME = stringPreferencesKey("theme_mode")
        val ACCENT = stringPreferencesKey("accent")
        val HAPTICS = booleanPreferencesKey("haptics")
        val GROUP_BY_CATEGORY = booleanPreferencesKey("group_by_category")
    }

    val settings: Flow<AppSettings> = store.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { prefs ->
            val defaults = AppSettings()
            AppSettings(
                themeMode = prefs[Keys.THEME].toEnumOrNull<ThemeMode>() ?: defaults.themeMode,
                accent = prefs[Keys.ACCENT].toEnumOrNull<AccentColor>() ?: defaults.accent,
                haptics = prefs[Keys.HAPTICS] ?: defaults.haptics,
                groupByCategory = prefs[Keys.GROUP_BY_CATEGORY] ?: defaults.groupByCategory,
            )
        }

    suspend fun setThemeMode(mode: ThemeMode) = store.edit { it[Keys.THEME] = mode.name }
    suspend fun setAccent(accent: AccentColor) = store.edit { it[Keys.ACCENT] = accent.name }
    suspend fun setHaptics(enabled: Boolean) = store.edit { it[Keys.HAPTICS] = enabled }
    suspend fun setGroupByCategory(enabled: Boolean) = store.edit { it[Keys.GROUP_BY_CATEGORY] = enabled }
}

private inline fun <reified T : Enum<T>> String?.toEnumOrNull(): T? =
    this?.let { value -> enumValues<T>().firstOrNull { it.name == value } }
