package com.upakon.moonlog.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

interface CalendarRepository {

    fun getDates(
        yearMonth: YearMonth,
        startingDay: DayOfWeek = DayOfWeek.SUNDAY,
        selected: LocalDate = LocalDate.now()
    ) : List<CalendarState.Date>

}