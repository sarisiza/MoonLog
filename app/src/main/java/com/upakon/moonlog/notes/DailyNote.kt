package com.upakon.moonlog.notes

import com.google.gson.Gson
import com.upakon.moonlog.database.model.DayEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

data class DailyNote(
    val day: LocalDate = LocalDate.now(),
    val feeling: Feeling? = null,
    val isPeriod: Boolean = false,
    val notes : MutableMap<String,Any> = mutableMapOf(),
    val journal: String? = null
){

    fun toDatabase(): DayEntity{
        return DayEntity(
            day.format(shortFormat),
            Gson().toJson(feeling),
            isPeriod,
            Gson().toJson(notes),
            journal
        )
    }

    companion object{
        val shortFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val longFormat: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)
    }

}
