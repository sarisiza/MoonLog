package com.upakon.moonlog.calendar

import android.util.Log
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.settings.UserSettings
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters
import kotlin.time.measureTime

private const val TAG = "CalendarRepositoryImpl"
class CalendarRepositoryImpl : CalendarRepository {
    override fun getDates(
        yearMonth: YearMonth,
        startingDay: DayOfWeek,
        selected: LocalDate,
        notes : Map<LocalDate,DailyNote>,
        userSettings: UserSettings
    ): List<CalendarState.Date> {
        Log.d(TAG, "getDates - selected: ${selected.format(DailyNote.formatter)}")
        return yearMonth.getDaysOfMonth(startingDay)
            .map {date ->
                val dayOfMonth = if(date.monthValue == yearMonth.monthValue){
                    "${date.dayOfMonth}"
                } else ""
                CalendarState.Date(
                    dayOfMonth,
                    date.isEqual(selected),
                    notes[date]?.isPeriod?:false,
                    date.couldBePeriod(userSettings)
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

    private fun LocalDate.couldBePeriod(userSettings: UserSettings) : Boolean {
        if(isBefore(LocalDate.now()))
            return false
        val difference = Duration.between(userSettings.lastPeriod,this).toDays()
        val multiple = difference/userSettings.cycleDuration
        val firstDay = userSettings.cycleDuration*multiple
        return (difference >= firstDay && difference < firstDay+userSettings.periodDuration)
    }

}