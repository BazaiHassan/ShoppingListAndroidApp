package com.example.dailyshoppinglist

import android.app.Application
import android.content.Context
import com.example.dailyshoppinglist.data.ShoppingRepository
import com.example.dailyshoppinglist.data.db.AppDatabase
import com.example.dailyshoppinglist.data.settings.SettingsRepository

class ShoppingApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}

/** Minimal manual dependency container. */
class AppContainer(context: Context) {
    private val database by lazy { AppDatabase.build(context) }
    val repository by lazy { ShoppingRepository(database) }
    val settingsRepository by lazy { SettingsRepository(context) }
}
