package com.upakon.moonlog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upakon.moonlog.calendar.CalendarRepository
import com.upakon.moonlog.calendar.CalendarState
import com.upakon.moonlog.database.repository.DatabaseRepository
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.settings.PreferencesStore
import com.upakon.moonlog.settings.UserSettings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Period
import java.time.YearMonth

/**
 * [MoonLogViewModel] ViewModel of the application
 *
 * Saves the state of the app and connects the data layer with the UI layer
 *
 * @constructor
 * @param settingsStore Interface to get settings from the DataStore
 * @param dispatcher Coroutine Dispatcher for background operations
 */

private const val TAG = "MoonLogViewModel"
class MoonLogViewModel(
    private val settingsStore: PreferencesStore,
    private val database: DatabaseRepository,
    private val calendar: CalendarRepository,
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

    private val _currentDay: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val currentDay : StateFlow<LocalDate> get() = _currentDay

    private var currentYearMonth = YearMonth.now()
    private val _calendarState : MutableStateFlow<CalendarState> = MutableStateFlow(
        CalendarState(
            currentYearMonth,
            calendar.getDates(currentYearMonth, selected = currentDay.value)
        )
    )
    val calendarState : StateFlow<CalendarState> get() = _calendarState


    /**
     * Method to get the user settings from the DataStore
     */
    fun downloadUserSettings(){
        viewModelScope.launch(dispatcher) {
            _userSettings.update { UiState.LOADING }
            try {
                Log.d(TAG, "downloadUserSettings: downloading")
                settingsStore.getSettings().collect{settings ->
                    _userSettings.update { UiState.SUCCESS(settings) }
                    currentSettings = settings
                    getCalendar(currentDay.value)
                }
            } catch (e: Exception){
                _userSettings.update{ UiState.ERROR(e) }
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
        note?.let {dNote ->
            _dailyNote.update{
                UiState.SUCCESS(dNote)
            }
        } ?: run {
            viewModelScope.launch(dispatcher) {
                database.readNote(day).collect{ note ->
                    if(note is UiState.SUCCESS){
                        notesCache[day] = note.data
                    }
                    _dailyNote.update {
                        note
                    }
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
                _feelingsList.update { it }
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

    /**
     * Method to go to next month
     */
    fun nextMonth(){
        currentYearMonth = currentYearMonth.plusMonths(1)
        getCalendar(currentDay.value)
    }

    /**
     * Method to go to previous month
     */
    fun previousMonth(){
        currentYearMonth = currentYearMonth.minusMonths(1)
        getCalendar(currentDay.value)
    }

    /**
     * Method to set current day
     *
     * @param day New date
     */
    fun setDay(day: CalendarState.Date){
        try {
            val state = calendarState.value
            _currentDay.value =
                LocalDate.of(
                    state.yearMonth.year,
                    state.yearMonth.month,
                    day.dayOfMonth.toInt()
                )
            getCalendar(currentDay.value)
        } catch (e: Exception){
            Log.d(TAG, "Error parsing state: ${e.localizedMessage}",e)
        }
    }

    private fun getCalendar(selected : LocalDate){
        Log.d(TAG, "getCalendar: ${selected.format(DailyNote.formatter)}")
        val state = CalendarState(
            currentYearMonth,
            calendar.getDates(
                currentYearMonth,
                currentSettings?.firstDayOfWeek ?: DayOfWeek.SUNDAY,
                selected
            )
        )
        _calendarState.value =  state
    }

}