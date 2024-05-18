package com.upakon.moonlog.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.upakon.moonlog.database.repository.DatabaseRepository
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.utils.UiState
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
    private val database: DatabaseRepository,
    private val dispatcher : CoroutineDispatcher
) : ViewModel() {

    private val _userSettings: MutableStateFlow<UiState<UserSettings>> =
        MutableStateFlow(UiState.LOADING)
    val userSettings: StateFlow<UiState<UserSettings>> get() = _userSettings
    private var currentSettings : UserSettings? = null
    private val _dailyNote: MutableStateFlow<UiState<DailyNote>> =
        MutableStateFlow(UiState.LOADING)
    val dailyNote: StateFlow<UiState<DailyNote>> get() = _dailyNote
    private val _feelingsList : MutableStateFlow<UiState<List<Feeling>>> =
        MutableStateFlow(UiState.LOADING)
    val feelingsList : StateFlow<UiState<List<Feeling>>> get() = _feelingsList

    //notes cache
    private val notesCache : MutableMap<LocalDate,DailyNote> = mutableMapOf()

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
    fun updatePregnancy(pregnant: Boolean){
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

    /**
     * Method to save a note to the database
     *
     * @param note Note to save
     */
    fun saveDailyNote(note: DailyNote){
        viewModelScope.launch(dispatcher) {
            notesCache[note.day] = note
            database.writeNote(note)
        }
    }

    /**
     * Method to retrieve a note
     *
     * @param day Day to retrieve
     */
    fun getDailyNote(day: LocalDate){
        val note = notesCache[day]
        note?.let {
            _dailyNote.value = UiState.SUCCESS(it)
        } ?: run {
            viewModelScope.launch(dispatcher) {
                database.readNote(day).collect{
                    if(it is UiState.SUCCESS){
                        notesCache[day] = it.data
                    }
                    _dailyNote.value = it
                }
            }
        }
    }

    /**
     * Method to delete a note
     *
     * @param note Note to delete
     */
    fun deleteNote(note: DailyNote){
        notesCache.remove(note.day)
        viewModelScope.launch(dispatcher) {
            database.deleteNote(note)
        }
    }

    /**
     * Method to add a feeling
     *
     * @param feeling Feeling to add
     */
    fun addFeeling(feeling: Feeling){
        viewModelScope.launch(dispatcher) {
            database.addFeeling(feeling)
        }
    }

    /**
     * Method to get the list of feelings
     */
    fun getFeelings(){
        viewModelScope.launch(dispatcher) {
            database.getFeelings().collect{
                _feelingsList.value = it
            }
        }
    }

    /**
     * Method to delete a feeling
     *
     * @param feeling Feeling to delete
     */
    fun deleteFeeling(feeling: Feeling){
        viewModelScope.launch(dispatcher) {
            database.deleteFeeling(feeling)
        }
    }

}