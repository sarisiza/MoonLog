package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.calendar.CalendarState
import com.upakon.moonlog.ui.theme.Purple2
import com.upakon.moonlog.ui.theme.Purple1
import com.upakon.moonlog.viewmodel.MoonLogViewModel

@Composable
fun CalendarScreen(
    viewModel: MoonLogViewModel,
    textSize: TextSize
) {
    Column {

    }
}

@Composable
fun MonthView(
    daysList: List<CalendarState.Date>,
    textSize: TextSize,
    onSelected: (CalendarState.Date) -> Unit
){
    Column {
        var index = 0
        repeat(6){
            if( index >= daysList.size) return@repeat
            Row {
                repeat(7){
                    val item = if(index < daysList.size) daysList[index] else CalendarState.Date()
                    DayView(
                        day = item,
                        textSize = textSize,
                        onSelected = onSelected
                    )
                    index ++
                }
            }
        }
    }
}

@Composable
fun DayView(
    day: CalendarState.Date,
    textSize: TextSize,
    onSelected : (CalendarState.Date) -> Unit
){
    Box(
        modifier = Modifier
            .background(
                color = if (day.isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
            )
            .clickable(
                enabled = day.dayOfMonth.isNotEmpty()
            ) {
                onSelected(day)
            }
    ) {
        Text(
            text = day.dayOfMonth,
            fontSize = textSize.textSize,
            color = if(day.isSelected){
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    }
}