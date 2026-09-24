package com.example.dailyshoppinglist

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailyshoppinglist.ui.ShoppingAppRoot
import com.example.dailyshoppinglist.ui.settings.AppLanguage
import com.example.dailyshoppinglist.ui.theme.ShoppingTheme
import com.example.dailyshoppinglist.ui.theme.isDark

class MainActivity : AppCompatActivity() {

    private var settingsLoaded = false

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)
        // Keep the splash until the theme is known, so there is no light/dark flash.
        splash.setKeepOnScreenCondition { !settingsLoaded }
        enableEdgeToEdge()
        applyDefaultLanguage()

        val settingsFlow = (application as ShoppingApplication).container.settingsRepository.settings
        setContent {
            val settings by settingsFlow.collectAsStateWithLifecycle(initialValue = null)
            val current = settings ?: return@setContent
            SideEffect { settingsLoaded = true }

            val dark = current.themeMode.isDark()
            DisposableEffect(dark) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                    navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { dark },
                )
                onDispose {}
            }

            ShoppingTheme(themeMode = current.themeMode, accent = current.accent, hapticsEnabled = current.haptics) {
                ShoppingAppRoot(widthSizeClass = calculateWindowSizeClass(this).widthSizeClass)
            }
        }
    }

    /**
     * Persian is the default language, even on devices set to English. It is applied once
     * (first launch); after that the choice made in Settings is kept by AppCompat.
     */
    private fun applyDefaultLanguage() {
        val prefs = getSharedPreferences("app", MODE_PRIVATE)
        if (prefs.getBoolean(KEY_LANGUAGE_INITIALIZED, false)) return
        prefs.edit().putBoolean(KEY_LANGUAGE_INITIALIZED, true).apply()
        if (AppCompatDelegate.getApplicationLocales().isEmpty) AppLanguage.DEFAULT.activate()
    }

    private companion object {
        const val KEY_LANGUAGE_INITIALIZED = "language_initialized"
    }
}
