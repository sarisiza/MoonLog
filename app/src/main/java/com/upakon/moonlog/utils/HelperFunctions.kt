package com.upakon.moonlog.utils

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

/**
 * Function to convert EPOCH to LocalDate
 *
 * @return date from epoch milis
 */
fun Long?.toLocalDate(): LocalDate {
    return this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .plusDays(1)
    } ?: LocalDate.now()
}

fun YearMonth.getDisplayName() : String{
    return "${month.getDisplayName(TextStyle.FULL, Locale.getDefault())} $year"
}

fun List<DayOfWeek>.sortByFirst(firstDay : DayOfWeek) : List<DayOfWeek>{
    val result = mutableListOf<DayOfWeek>()
    result.addAll(subList(indexOf(firstDay),size))
    result.addAll(subList(0,indexOf(firstDay)))
    return result
}

fun LocalDate.isInMonth(yearMonth: YearMonth) : Boolean{
    return month == yearMonth.month && year == yearMonth.year
}