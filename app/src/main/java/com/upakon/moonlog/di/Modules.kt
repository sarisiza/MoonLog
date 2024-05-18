package com.upakon.moonlog.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.upakon.moonlog.calendar.CalendarRepository
import com.upakon.moonlog.calendar.CalendarRepositoryImpl
import com.upakon.moonlog.database.repository.DatabaseRepository
import com.upakon.moonlog.database.repository.DatabaseRepositoryImpl
import com.upakon.moonlog.database.room.DailyDatabase
import com.upakon.moonlog.database.room.DayDao
import com.upakon.moonlog.settings.PreferencesStore
import com.upakon.moonlog.settings.PreferencesStoreImpl
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * File that defines the modules to be injected with Koin
 */

private const val USER_SETTINGS = "user_settings"

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_SETTINGS
)

/**
 * Module to get dependencies for the data layer
 */
val dataModule = module {
    //data store
    single<PreferencesStore> {
        PreferencesStoreImpl(androidContext().dataStore)
    }
    //database
    single<DailyDatabase> {
        Room.databaseBuilder(
            androidContext(),
            DailyDatabase::class.java,
            "notesDatabase"
        ).build()
    }
    //dao
    single<DayDao> {
        get<DailyDatabase>().getDao()
    }
    //database repository
    single<DatabaseRepository> {
        DatabaseRepositoryImpl(
            get()
        )
    }
    //calendar repository
    single<CalendarRepository> {
        CalendarRepositoryImpl()
    }
}

/**
 * Module to get view model dependencies
 */
val viewModelModule = module {
    viewModel {
        MoonLogViewModel(
            settingsStore = get(),
            database = get (),
            calendar = get(),
            dispatcher = Dispatchers.IO
        )
    }
}