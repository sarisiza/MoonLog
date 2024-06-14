package com.upakon.moonlog.database.repository

import android.util.Log
import com.upakon.moonlog.database.model.toFeelings
import com.upakon.moonlog.database.room.DayDao
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import java.time.YearMonth

private const val TAG = "DatabaseRepositoryImpl"
class DatabaseRepositoryImpl(
    private val dao: DayDao
) : DatabaseRepository {
    override suspend fun writeNote(note: DailyNote){
        Log.d(TAG, "writing: ${note.day}")
        val entry = note.toDatabase()
        try {
            dao.writeNotes(entry)
        } catch (e: Exception){
            Log.e(TAG, "writeNote: ${e.localizedMessage}", e)
        }
    }

    override fun readNote(day: LocalDate): Flow<DailyNote> = flow {
        try {
            Log.d(TAG, "readNote: reading notes")
            val dayS = day.format(DailyNote.shortFormat)
            emit(dao.readNote(dayS)?.toDailyNote() ?: DailyNote(day))
        } catch (e: Exception){
            Log.e(TAG, "readNote: ${e.localizedMessage}", e)
            throw e
        }
    }

    override fun readMonthlyNotes(month: YearMonth): Flow<List<DailyNote>> = flow {
        try {
            Log.d(TAG, "readMonthlyNotes: reading notes")
            val result = mutableListOf<DailyNote>()
            for (i in 1 .. month.lengthOfMonth()){
                val dayS = month.atDay(i).format(DailyNote.shortFormat)
                Log.d(TAG, "readMonthlyNotes: $dayS")
                result.add(dao.readNote(dayS)?.toDailyNote() ?: DailyNote(month.atDay(i)))
            }
            Log.d(TAG, "result size: ${result.size}")
            emit(result)
        } catch (e: Exception){
            Log.e(TAG, "readMonthlyNotes: ${e.localizedMessage}", e)
            throw e
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

    override fun getFeelings(): Flow<List<Feeling>> = flow {
        try {
            val feelings = dao.getFeelings().toFeelings()
            emit(feelings)
        } catch (e: Exception){
            throw e
        }
    }

    override suspend fun deleteFeeling(feeling: Feeling){
        val feelDb = feeling.toDatabase()
        dao.deleteFeeling(feelDb)
    }
}