package com.upakon.moonlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upakon.moonlog.Utils.UiState
import com.upakon.moonlog.settings.PreferencesStore
import com.upakon.moonlog.settings.UserSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

/**
 * [MoonLogViewModel] ViewModel of the application
 *
 * Saves the state of the app and connects the data layer with the UI layer
 *
 * @constructor
 * @param settingsStore Interface to get settings from the DataStore
 * @param dispatcher Coroutine Dispatcher for background operations
 */
class MoonLogViewModel(
    private val settingsStore: PreferencesStore,
    private val dispatcher : CoroutineDispatcher
) : ViewModel() {

    private val _userSettings: MutableStateFlow<UiState<UserSettings>> =
        MutableStateFlow(UiState.LOADING)
    val userSettings: StateFlow<UiState<UserSettings>> get() = _userSettings
    private var currentSettings : UserSettings? = null

    /**
     * Method to get the user settings from the DataStore
     */
    fun downloadUserSettings(){
        viewModelScope.launch(dispatcher) {
            _userSettings.value = UiState.LOADING
            try {
                settingsStore.getSettings().collect{
                    _userSettings.value = UiState.SUCCESS(it)
                    currentSettings = it
                }
            } catch (e: Exception){
                _userSettings.value = UiState.ERROR(e)
            }
        }
    }

    /**
     * Method to save the UserSettings in the data store
     *
     * @param userSettings The settings to save
     */
    fun saveUserSettings(userSettings: UserSettings){
        viewModelScope.launch(dispatcher) {
            settingsStore.saveSettings(userSettings)
        }
    }

    /**
     * Method to update the latest period in the data store
     *
     * @param periodDate date of the new period
     */
    private fun updateLatestPeriod(periodDate: LocalDate){
        val newSettings = currentSettings?.let {settings ->
            UserSettings(
                settings.username,
                periodDate,
                settings.periodDuration,
                settings.cycleDuration,
                settings.pregnant
            )
        } ?: UserSettings(lastPeriod = periodDate)
        currentSettings = newSettings
        saveUserSettings(newSettings)
    }

    /**
     * Method to update pregnancy status
     *
     * @param pregnant pregnancy status
     */
    private fun updatePregnancy(pregnant: Boolean){
        val newSettings = currentSettings?.let {settings ->
            UserSettings(
                settings.username,
                settings.lastPeriod,
                settings.periodDuration,
                settings.cycleDuration,
                pregnant
            )
        } ?: UserSettings(pregnant = pregnant)
        currentSettings = newSettings
        saveUserSettings(newSettings)
    }

}