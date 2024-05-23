package com.upakon.moonlog.calendar

import java.time.LocalDate
import java.time.YearMonth

data class CalendarState(
    val yearMonth: YearMonth,
    val dates: List<Date>
) {
    data class Date(
        val dayOfMonth : String = "",
        var isSelected: Boolean = false
    )

    fun updateSelected(date: Date){
        dates.forEach {
            if (it == date){
                it.isSelected = true
            } else {
                if (it.isSelected)
                    it.isSelected = false
            }
        }
    }

}