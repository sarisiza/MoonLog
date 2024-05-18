package com.upakon.moonlog.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import java.time.LocalDate

@Entity(tableName = "dailyNotes")
data class DayEntity(
    @PrimaryKey val day: String,
    val feeling: String?,
    val isPeriod: Boolean,
    val intercourse: Boolean,
    val wasProtected: Boolean,
    val journal: String?
){

    fun toDailyNote(): DailyNote{
        return DailyNote(
            LocalDate.parse(day,DailyNote.formatter),
            Gson().fromJson(feeling,Feeling::class.java),
            isPeriod,
            intercourse,
            wasProtected,
            journal
        )
    }

}
