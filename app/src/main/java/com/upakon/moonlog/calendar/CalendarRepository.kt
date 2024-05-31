package com.upakon.moonlog.calendar

import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.settings.UserSettings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

interface CalendarRepository {

    fun getDates(
        yearMonth: YearMonth,
        startingDay: DayOfWeek = DayOfWeek.SUNDAY,
        selected: LocalDate = LocalDate.now(),
        notes : Map<LocalDate,DailyNote>,
        userSettings: UserSettings
    ) : List<CalendarState.Date>

}