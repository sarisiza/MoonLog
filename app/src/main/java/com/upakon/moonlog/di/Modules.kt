package com.upakon.moonlog.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.upakon.moonlog.settings.PreferencesStore
import com.upakon.moonlog.settings.PreferencesStoreImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * File that defines the modules to be injected with Koin
 */

private const val USER_SETTINGS = "user_settings"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_SETTINGS
)

val preferencesStoreModule = module {
    single<PreferencesStore> {
        PreferencesStoreImpl(androidContext().dataStore)
    }
}