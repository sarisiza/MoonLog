package com.upakon.moonlog.calendar

import android.util.Log
import com.upakon.moonlog.notes.DailyNote
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

private const val TAG = "CalendarRepositoryImpl"
class CalendarRepositoryImpl : CalendarRepository {
    override fun getDates(
        yearMonth: YearMonth,
        startingDay: DayOfWeek,
        selected: LocalDate
    ): List<CalendarState.Date> {
        Log.d(TAG, "getDates - selected: ${selected.format(DailyNote.formatter)}")
        return yearMonth.getDaysOfMonth(startingDay)
            .map {
                val dayOfMonth = if(it.monthValue == yearMonth.monthValue){
                    "${it.dayOfMonth}"
                } else ""
                CalendarState.Date(
                    dayOfMonth,
                    it.isEqual(selected)
                )
            }
    }

    private fun YearMonth.getDaysOfMonth(starting: DayOfWeek) : List<LocalDate>{
        val firstDay = LocalDate.of(year,month,1)
        val firstWeekDay = firstDay.with(TemporalAdjusters.previous(starting))
        val firstDayOfNextMonth = firstDay.plusMonths(1)

        return generateSequence(firstWeekDay) { it.plusDays(1) }
            .takeWhile { it.isBefore(firstDayOfNextMonth) }
            .toList()
    }

}