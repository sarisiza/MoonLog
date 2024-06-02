package com.upakon.moonlog.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * [PreferencesStoreImpl] implementation of the methods from [PreferencesStore]
 *
 * @constructor
 * @param dataStore DataStore object for saving the preferences
 *
 */
class PreferencesStoreImpl(
    private val dataStore: DataStore<Preferences>
) : PreferencesStore {

    private val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

    override fun getSettings(): Flow<UserSettings> =
        dataStore.data.map {preferences ->
            val username = preferences[PreferencesStore.USERNAME]
            val lastPeriod = preferences[PreferencesStore.LAST_PERIOD]?.let {date ->
                LocalDate.parse(date, formatter)
            }
            val periodDuration = preferences[PreferencesStore.PERIOD_DURATION]
            val cycleDuration = preferences[PreferencesStore.CYCLE_DURATION]
            val pregnant = preferences[PreferencesStore.PREGNANT]
            val dayOfWeek = preferences[PreferencesStore.DAY_OF_WEEK]?.let {
                DayOfWeek.of(it)
            }
            UserSettings(username, lastPeriod, periodDuration, cycleDuration, pregnant, dayOfWeek)
        }

    override suspend fun saveSettings(settings: UserSettings) {
        dataStore.edit {
            it[PreferencesStore.USERNAME] = settings.username!!
            it[PreferencesStore.LAST_PERIOD] = settings.lastPeriod!!.format(formatter)
            it[PreferencesStore.PERIOD_DURATION] = settings.periodDuration!!
            it[PreferencesStore.CYCLE_DURATION] = settings.cycleDuration!!
            it[PreferencesStore.PREGNANT] = false //todo change when ready
            it[PreferencesStore.DAY_OF_WEEK] = settings.firstDayOfWeek!!.value
        }
    }


}