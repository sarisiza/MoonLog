package com.upakon.moonlog.calendar

import java.time.DayOfWeek
import java.time.YearMonth

interface CalendarRepository {

    fun getDates(yearMonth: YearMonth, startingDay: DayOfWeek) : List<CalendarState.Date>

}