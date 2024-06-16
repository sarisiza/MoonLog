package com.upakon.moonlog.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.utils.parseJsonToMap
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import java.time.LocalDate

@Entity(tableName = "dailyNotes")
data class DayEntity(
    @PrimaryKey val day: String,
    val feeling: String?,
    val isPeriod: Boolean,
    val notes: String,
    val journal: String?
){

    fun toDailyNote(): DailyNote{
        return DailyNote(
            LocalDate.parse(day,DailyNote.shortFormat),
            Gson().fromJson(feeling,Feeling::class.java),
            isPeriod,
            Json.encodeToJsonElement(notes).parseJsonToMap(),
            journal
        )
    }

}
