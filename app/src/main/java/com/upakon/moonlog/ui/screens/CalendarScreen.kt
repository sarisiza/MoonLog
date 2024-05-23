package com.upakon.moonlog.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upakon.moonlog.R
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.calendar.CalendarState
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.utils.getDisplayName
import com.upakon.moonlog.utils.sortByFirst
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.log

private const val TAG = "CalendarScreen"
@Composable
fun CalendarScreen(
    viewModel: MoonLogViewModel,
    textSize: TextSize
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val calendar = viewModel.calendarState.collectAsState().value
        val currentDate = viewModel.currentDay.collectAsState().value
        val userSettings = (viewModel.userSettings.collectAsState().value as UiState.SUCCESS).data
        val note = "Selected date: ${currentDate.format(DailyNote.formatter)}"
        MonthHeader(
            yearMonth = calendar.yearMonth,
            viewModel = viewModel,
            textSize = textSize
        )
        WeekHeader(textSize = textSize, start = userSettings.firstDayOfWeek)
        MonthView(
            daysList = calendar.dates,
            textSize = textSize
        ) {
            viewModel.setDay(it)
        }
        Text(
            text = note,
            fontSize = textSize.titleSize
        )
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
            Row(
            ) {
                repeat(7){
                    val item = if(index < daysList.size) daysList[index] else CalendarState.Date()
                    DayView(
                        day = item,
                        textSize = textSize,
                        modifier = Modifier
                            .weight(1F),
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
    modifier: Modifier = Modifier,
    onSelected : (CalendarState.Date) -> Unit
){
    Card(
        modifier = modifier
            .clickable(
                enabled = day.dayOfMonth.isNotEmpty()
            ) {
                onSelected(day)
            }
            .padding(4.dp),
        colors = CardColors(
            containerColor = if (day.dayOfMonth.isEmpty()) {
                Color.Transparent
            } else if (day.isSelected) {
                Log.d(TAG, "Selected: ${day.dayOfMonth}")
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
            contentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Transparent
        ),
        border = BorderStroke(
            width = 10.dp,
            color = Color.Transparent
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()){
            Text(
                text = day.dayOfMonth,
                fontSize = textSize.textSize,
                color = if(day.isSelected){
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.primary
                },
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun MonthHeader(
    yearMonth: YearMonth,
    viewModel: MoonLogViewModel,
    textSize: TextSize
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        IconButton(
            onClick = {
                viewModel.previousMonth()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(id = R.string.back))
        }
        Text(
            text = yearMonth.getDisplayName(),
            textAlign = TextAlign.Center,
            fontSize = textSize.headerSize,
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        IconButton(
            onClick = {
                viewModel.nextMonth()
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(id = R.string.next))
        }
    }
}

@Composable
fun WeekHeader(
    textSize: TextSize,
    start: DayOfWeek
){
    val days = DayOfWeek.entries.toList().sortByFirst(start).map {
        it.getDisplayName(TextStyle.NARROW, Locale.getDefault())
    }
    Row {
        for(day in days){
            Box(
                modifier = Modifier
                    .weight(1F)
            ){
                Text(
                    text = day,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(10.dp),
                    fontSize = textSize.titleSize
                )
            }
        }
    }
}

