package com.upakon.moonlog.settings

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * [PreferencesStore] Interface that defines the methods used to get information from the Data Store
 */

interface PreferencesStore {

    /**
     * Method to get settings from Data Store
     *
     * @return User Settings
     */
    fun getSettings() : Flow<UserSettings>

    /**
     * Method to save the settings to Data store
     *
     * @param settings User Settings to be saved
     */
    suspend fun saveSettings(settings: UserSettings)

    /**
     * Keys for the Data Store
     */
    companion object{
        val USERNAME = stringPreferencesKey("username")
        val LAST_PERIOD = stringPreferencesKey("last_period")
        val PERIOD_DURATION = intPreferencesKey("period_duration")
        val CYCLE_DURATION = intPreferencesKey("cycle_duration")
    }

}