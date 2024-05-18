package com.upakon.moonlog.notes

import com.google.gson.Gson
import com.upakon.moonlog.database.model.DayEntity
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DailyNote(
    val day: LocalDate = LocalDate.now(),
    val feeling: Feeling? = null,
    val isPeriod: Boolean = false,
    val intercourse: Boolean = false,
    val protected: Boolean = false,
    val journal: String? = null
){

    fun toDatabase(): DayEntity{
        return DayEntity(
            day.format(formatter),
            Gson().toJson(feeling),
            isPeriod,
            intercourse,
            protected,
            journal
        )
    }

    companion object{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    }

}
