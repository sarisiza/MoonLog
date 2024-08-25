package com.upakon.moonlog.database.repository

import com.upakon.moonlog.database.model.TrackerEntity
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.notes.Tracker
import com.upakon.moonlog.utils.UiState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface DatabaseRepository {

    suspend fun writeNote(note: DailyNote)

    fun readNote(day: LocalDate) : Flow<DailyNote>

    fun readMonthlyNotes(month: YearMonth) : Flow<List<DailyNote>>

    suspend fun deleteNote(note: DailyNote)

    suspend fun addFeeling(feeling: Feeling)

    fun getFeelings() : Flow<List<Feeling>>

    suspend fun deleteFeeling(feeling: Feeling)

    fun getTrackers() : Flow<List<Tracker>>

    suspend fun addTracker(tracker: Tracker)

    suspend fun deleteTracker(tracker: Tracker)

}