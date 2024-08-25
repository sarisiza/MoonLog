package com.upakon.moonlog.calendar

import android.util.Log
import java.time.LocalDate
import java.time.YearMonth

private const val TAG = "CalendarState"
data class CalendarState(
    val yearMonth: YearMonth,
    val dates: List<Date>
) {
    data class Date(
        val dayOfMonth : String = "",
        var isSelected: Boolean = false,
        var isPeriod: Boolean = false,
        var nextPeriod: Boolean = false
    )

}