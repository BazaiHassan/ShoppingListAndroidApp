package com.example.dailyshoppinglist.ui.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.dailyshoppinglist.ShoppingApplication
import com.example.dailyshoppinglist.data.ShoppingRepository
import com.example.dailyshoppinglist.data.settings.AccentColor
import com.example.dailyshoppinglist.data.settings.AppSettings
import com.example.dailyshoppinglist.data.settings.SettingsRepository
import com.example.dailyshoppinglist.data.settings.ThemeMode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppLanguage(val tag: String?) {
    SYSTEM(null),
    PERSIAN("fa"),
    ENGLISH("en"),
    ;

    companion object {
        fun current(): AppLanguage {
            val locales = AppCompatDelegate.getApplicationLocales()
            if (locales.isEmpty) return SYSTEM
            return when (locales[0]?.language) {
                "fa" -> PERSIAN
                "en" -> ENGLISH
                else -> SYSTEM
            }
        }
    }
}

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val repository: ShoppingRepository,
) : ViewModel() {
    val settings: StateFlow<AppSettings> =
        settingsRepository.settings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    fun setThemeMode(mode: ThemeMode) = launch { settingsRepository.setThemeMode(mode) }
    fun setAccent(accent: AccentColor) = launch { settingsRepository.setAccent(accent) }
    fun setHaptics(enabled: Boolean) = launch { settingsRepository.setHaptics(enabled) }
    fun setGroupByCategory(enabled: Boolean) = launch { settingsRepository.setGroupByCategory(enabled) }
    fun clearHistory() = launch { repository.clearHistory() }

    /** Per-app language (stored by AppCompat; also shown in system settings on Android 13+). */
    fun setLanguage(language: AppLanguage) {
        val locales = language.tag?.let { LocaleListCompat.forLanguageTags(it) } ?: LocaleListCompat.getEmptyLocaleList()
        AppCompatDelegate.setApplicationLocales(locales)
    }

    private fun launch(block: suspend () -> Unit) {
        viewModelScope.launch { block() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ShoppingApplication
                SettingsViewModel(app.container.settingsRepository, app.container.repository)
            }
        }
    }
}
