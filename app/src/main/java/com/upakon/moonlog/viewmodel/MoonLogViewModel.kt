package com.upakon.moonlog.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.upakon.moonlog.calendar.CalendarRepository
import com.upakon.moonlog.calendar.CalendarState
import com.upakon.moonlog.database.repository.DatabaseRepository
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.notes.Tracker
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.settings.PreferencesStore
import com.upakon.moonlog.settings.UserSettings
import com.upakon.moonlog.utils.isInMonth
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

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
    var currentSettings : UserSettings? = null
        private set

    val _monthlyNotes: MutableStateFlow<UiState<List<DailyNote>>> =
        MutableStateFlow(UiState.LOADING)
    val monthlyNotes: StateFlow<UiState<List<DailyNote>>> get() = _monthlyNotes

    private val _feelingsList : MutableStateFlow<UiState<List<Feeling>>> =
        MutableStateFlow(UiState.LOADING)
    val feelingsList : StateFlow<UiState<List<Feeling>>> get() = _feelingsList
    private val feelingsCache : StateFlow<MutableList<Feeling>> = MutableStateFlow(mutableListOf())

    private val _trackersList : MutableStateFlow<UiState<List<Tracker>>> =
        MutableStateFlow(UiState.LOADING)
    val trackersList : StateFlow<UiState<List<Tracker>>> get() = _trackersList
    private val trackersCache : StateFlow<MutableList<Tracker>> = MutableStateFlow(mutableListOf())

    //notes cache
    private val _notesState : StateFlow<MutableMap<LocalDate,DailyNote>> =
        MutableStateFlow(
            mutableMapOf()
        )
    val notesState : StateFlow<Map<LocalDate,DailyNote>> get() = _notesState

    private val _currentDay: MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val currentDay : StateFlow<LocalDate> get() = _currentDay

    private var currentYearMonth = YearMonth.now()
    private val _calendarState : MutableStateFlow<CalendarState?> = MutableStateFlow(
        null
    )
    val calendarState : StateFlow<CalendarState?> get() = _calendarState


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
                    Log.d(TAG, "downloadUserSettings: ${currentSettings}")
                }
            } catch (e: Exception){
                Log.e(TAG, "downloadUserSettings: ${e.localizedMessage}", )
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
            if(userSettings.lastPeriod != null && userSettings.periodDuration != null)
                storeNewPeriod(userSettings.lastPeriod,userSettings.periodDuration)
            downloadUserSettings()
        }
    }

    /**
     * Method to update the latest period in the data store
     *
     * @param periodDate date of the new period
     */
    fun updateLatestPeriod(periodDate: LocalDate){
        Log.d(TAG, "updateLatestPeriod: $currentSettings")
        val newSettings = currentSettings?.let {settings ->
            UserSettings(
                settings.username,
                periodDate,
                settings.periodDuration,
                settings.cycleDuration,
                settings.pregnant,
                settings.firstDayOfWeek
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
                pregnant,
                settings.firstDayOfWeek
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
            _notesState.value[note.day] = note
            database.writeNote(note)
            getMonthlyNotes()
        }
    }

    /**
     * Method to retrieve the notes of the month
     */
    fun getMonthlyNotes(){
        _monthlyNotes.update { UiState.LOADING }
        viewModelScope.launch(dispatcher) {
            try {
                Log.d(TAG, "getMonthlyNotes: getting notes")
                val notesList = notesState.value.filterKeys {day ->
                    day.isInMonth(currentYearMonth)
                }.values.toList()
                if(notesList.isNotEmpty()){
                    _monthlyNotes.value = UiState.SUCCESS(notesList)
                }else {
                    database.readMonthlyNotes(currentYearMonth).collect { notes ->
                        notes.map { note ->
                            _notesState.value[note.day] = note
                        }
                        _monthlyNotes.value = UiState.SUCCESS(notes)
                    }
                }
                getCalendar(currentDay.value)
            } catch (e: Exception){
                Log.e(TAG, "getMonthlyNotes: ${e.localizedMessage}", )
                _monthlyNotes.update { UiState.ERROR(e) }
            }
        }
    }

    /**
     * Method to delete a note
     *
     * @param note Note to delete
     */
    fun deleteNote(note: DailyNote){
        _notesState.value.remove(note.day)
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
            feelingsCache.value.add(feeling)
            database.addFeeling(feeling)
        }
    }

    /**
     * Method to get the list of feelings
     */
    fun getFeelings(){
        _feelingsList.value = UiState.LOADING
        viewModelScope.launch(dispatcher) {
            try{
                if (feelingsCache.value.isNotEmpty()){
                    _feelingsList.value = UiState.SUCCESS(feelingsCache.value)
                } else {
                    database.getFeelings().collect { feelings ->
                        feelingsCache.value.addAll(feelings)
                        _feelingsList.value = UiState.SUCCESS(feelings)
                    }
                }
            } catch (e: Exception){
                Log.e(TAG, "getFeelings: ${e.localizedMessage}", )
                _feelingsList.value = UiState.ERROR(e)
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
            feelingsCache.value.remove(feeling)
            database.deleteFeeling(feeling)
        }
    }

    /**
     * Method to add a tracker
     *
     * @param tracker Tracker to add
     */
    fun addTracker(tracker: Tracker){
        viewModelScope.launch(dispatcher) {
            trackersCache.value.add(tracker)
            database.addTracker(tracker)
        }
    }

    fun getTrackers(){
        viewModelScope.launch(dispatcher) {
            try {
                _trackersList.value = UiState.LOADING
                if(trackersCache.value.isNotEmpty()){
                    _trackersList.value = UiState.SUCCESS(trackersCache.value)
                } else {
                    database.getTrackers().collect{
                        trackersCache.value.addAll(it)
                        _trackersList.value = UiState.SUCCESS(it)
                    }
                }
            } catch (e: Exception){
                Log.e(TAG, "getTrackers: ${e.localizedMessage}",e)
                _trackersList.value = UiState.ERROR(e)
            }
        }
    }

    /**
     * Method to add a tracker
     *
     * @param tracker Tracker to add
     */
    fun deleteTracker(tracker: Tracker){
        viewModelScope.launch(dispatcher) {
            trackersCache.value.remove(tracker)
            database.deleteTracker(tracker)
        }
    }

    /**
     * Method to go to next month
     */
    fun nextMonth(){
        currentYearMonth = currentYearMonth.plusMonths(1)
        getMonthlyNotes()
    }

    /**
     * Method to go to previous month
     */
    fun previousMonth(){
        currentYearMonth = currentYearMonth.minusMonths(1)
        getMonthlyNotes()
    }

    /**
     * Method to set current day
     *
     * @param day New date
     */
    fun setDay(day: CalendarState.Date){
        try {
            val state = calendarState.value
            state?.let {
            _currentDay.value =
                LocalDate.of(
                    it.yearMonth.year,
                    it.yearMonth.month,
                    day.dayOfMonth.toInt()
                )
            }
            getCalendar(currentDay.value)
        } catch (e: Exception){
            Log.d(TAG, "Error parsing state: ${e.localizedMessage}",e)
        }
    }

    fun goToToday(){
        _currentDay.value = LocalDate.now()
    }

    fun getDaysUntilNextPeriod(day : LocalDate) : Int {
        return currentSettings?.let {settings ->
            (settings.cycleDuration ?: 28) - getDaysFromPeriod(day)
        } ?: 0
    }

    fun getDaysFromPeriod(day: LocalDate) : Int {
        return currentSettings?.let {settings ->
            Duration.ofDays(ChronoUnit.DAYS.between(settings.lastPeriod,day)).toDays().toInt()
        } ?: 0
    }

    fun calculatePregnantChance(daysFrom : Int) : Boolean {
        return daysFrom in 12..16
    }

    private fun getCalendar(selected : LocalDate){
        Log.d(TAG, "getCalendar: ${selected.format(DailyNote.shortFormat)}")
        val state = CalendarState(
            currentYearMonth,
            calendar.getDates(
                currentYearMonth,
                currentSettings?.firstDayOfWeek ?: DayOfWeek.SUNDAY,
                selected,
                notesState.value,
                currentSettings ?: UserSettings()
            )
        )
        _calendarState.value =  state
    }

    private suspend fun storeNewPeriod(periodStart: LocalDate, periodDuration: Int){
        Log.d(TAG, "storeNewPeriod: storing period starting in ${periodStart.format(DailyNote.shortFormat)}")
        for(day in 0 until periodDuration){
            val currentDay = periodStart.plusDays(day.toLong())
            val note = notesState.value[currentDay] ?: database.readNote(currentDay).first()
            Log.d(TAG, "storeNewPeriod: ${note.day}")
            DailyNote(
                note.day,
                note.feeling,
                true,
                note.notes,
                note.journal
            ).also {newNote ->
                saveDailyNote(newNote)
            }
        }
    }

}