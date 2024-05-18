package com.upakon.moonlog.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

class CalendarRepositoryImpl : CalendarRepository {
    override fun getDates(yearMonth: YearMonth, startingDay: DayOfWeek): List<CalendarState.Date> {
        return yearMonth.getDaysOfMonth(startingDay)
            .map {
                val dayOfMonth = if(it.monthValue == yearMonth.monthValue){
                    "${it.dayOfMonth}"
                } else ""
                CalendarState.Date(
                    dayOfMonth
                )
            }
    }

    private fun YearMonth.getDaysOfMonth(starting: DayOfWeek) : List<LocalDate>{
        val firstDay = LocalDate.of(year,month,1)
        val firstWeekDay = firstDay.with(starting)
        val firstDayOfNextMonth = firstDay.plusMonths(1)

        return generateSequence(firstWeekDay) { it.plusDays(1) }
            .takeWhile { it.isBefore(firstDayOfNextMonth) }
            .toList()
    }

}