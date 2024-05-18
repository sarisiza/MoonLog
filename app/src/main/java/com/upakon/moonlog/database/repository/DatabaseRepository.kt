package com.upakon.moonlog.database.repository

import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.utils.UiState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface DatabaseRepository {

    suspend fun writeNote(note: DailyNote)

    fun readNote(day: LocalDate) : Flow<UiState<DailyNote>>

    suspend fun deleteNote(note: DailyNote)

    suspend fun addFeeling(feeling: Feeling)

    fun getFeelings() : Flow<UiState<List<Feeling>>>

    suspend fun deleteFeeling(feeling: Feeling)

}