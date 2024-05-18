package com.upakon.moonlog.database.repository

import com.upakon.moonlog.database.model.toFeelings
import com.upakon.moonlog.database.room.DayDao
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.utils.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate

class DatabaseRepositoryImpl(
    private val dao: DayDao
) : DatabaseRepository {
    override suspend fun writeNote(note: DailyNote){
        val entry = note.toDatabase()
        dao.writeNotes(entry)
    }

    override fun readNote(day: LocalDate): Flow<UiState<DailyNote>> = flow {
        emit(UiState.LOADING)
        try {
            val dayS = day.format(DailyNote.formatter)
            val entry = dao.readNote(dayS)?.toDailyNote() ?: DailyNote()
            emit(UiState.SUCCESS(entry))
        }catch (e: Exception){
            emit(UiState.ERROR(e))
        }

    }

    override suspend fun deleteNote(note: DailyNote) {
        val entry = note.toDatabase()
        dao.deleteNote(entry)
    }

    override suspend fun addFeeling(feeling: Feeling){
        val feelDb = feeling.toDatabase()
        dao.addFeeling(feelDb)
    }

    override fun getFeelings(): Flow<UiState<List<Feeling>>> = flow {
        emit(UiState.LOADING)
        try {
            val feelings = dao.getFeelings().toFeelings()
            emit(UiState.SUCCESS(feelings))
        } catch (e: Exception){
            emit(UiState.ERROR(e))
        }
    }

    override suspend fun deleteFeeling(feeling: Feeling){
        val feelDb = feeling.toDatabase()
        dao.deleteFeeling(feelDb)
    }
}