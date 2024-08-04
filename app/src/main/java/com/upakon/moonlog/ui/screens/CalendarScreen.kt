package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import com.upakon.moonlog.calendar.CalendarState
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.ui.theme.ColorFamily
import com.upakon.moonlog.ui.theme.extendedColors
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.utils.getDisplayName
import com.upakon.moonlog.utils.sortByFirst
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private const val TAG = "CalendarScreen"
@Composable
fun CalendarScreen(
    viewModel: MoonLogViewModel,
    onEmpty: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        var message by remember {
            mutableStateOf("")
        }
        var showError by remember {
            mutableStateOf(false)
        }
        when(val settings = viewModel.userSettings.collectAsState().value){
            is UiState.ERROR -> {
                //todo add analytics
                message = stringResource(id = R.string.settings_error)
                showError = true
            }
            UiState.LOADING -> {
                CircularProgressIndicator()
            }
            is UiState.SUCCESS -> {
                val nCalendar = viewModel.calendarState.collectAsState().value
                val currentDate = viewModel.currentDay.collectAsState().value
                val userSettings = settings.data
                val note = "Selected date: ${currentDate.format(DailyNote.shortFormat)}"
                var updatePeriod by remember {
                    mutableStateOf(false)
                }
                nCalendar?.let {calendar ->
                    MonthHeader(
                        yearMonth = calendar.yearMonth,
                        viewModel = viewModel
                    )
                    WeekHeader(start = userSettings.firstDayOfWeek!!)
                    MonthView(
                        daysList = calendar.dates
                    ) {day ->
                        if(day.isSelected){
                            if(!currentDate.isAfter(LocalDate.now()))
                                updatePeriod = true
                        } else{
                            viewModel.setDay(day)
                        }
                    }
                    NotesView(
                        viewModel = viewModel,
                        onEmpty
                    )
                    if(updatePeriod){
                        AlertDialog(
                            title = {
                                Text(
                                    text = stringResource(id = R.string.update_period),
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource(id = R.string.your_first_day),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            },
                            onDismissRequest = {
                                updatePeriod = false
                            },
                            confirmButton = {
                                Button(onClick = {
                                    viewModel.updateLatestPeriod(currentDate)
                                    updatePeriod = false
                                }) {
                                    Text(
                                        text = stringResource(id = R.string.yes),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            },
                            dismissButton = {
                                Button(onClick = { updatePeriod = false }) {
                                    Text(
                                        text = stringResource(id = R.string.no),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
        if(showError){
            ErrorMessage(
                message = message,
                onDismiss = { showError = false }
            ){
                viewModel.downloadUserSettings()
                showError = true
            }
        }
    }
}

@Composable
fun MonthView(
    daysList: List<CalendarState.Date>,
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
    modifier: Modifier = Modifier,
    onSelected : (CalendarState.Date) -> Unit
){
    val cardColor = getContainerColor(day = day)
    Card(
        modifier = modifier
            .clickable(
                enabled = day.dayOfMonth.isNotEmpty()
            ) {
                onSelected(day)
            }
            .padding(4.dp),
        colors = CardColors(
            containerColor = if(day.isSelected) cardColor.colorContainer else cardColor.color,
            contentColor = if(day.isSelected) cardColor.onColorContainer else cardColor.onColor,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.White
        ),
        border = BorderStroke(
            width = 10.dp,
            color = Color.Transparent
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()){
            Text(
                text = day.dayOfMonth,
                style = MaterialTheme.typography.bodyMedium,
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
    viewModel: MoonLogViewModel
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
            style = MaterialTheme.typography.headlineMedium,
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
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Composable
fun getContainerColor(
    day: CalendarState.Date
) : ColorFamily {

    return if (day.dayOfMonth.isEmpty())
        ColorFamily(
            Color.Transparent,
            Color.Transparent,
            Color.Transparent,
            Color.Transparent
        )
    else if (day.isPeriod)
        extendedColors.customColor1
    else if (day.nextPeriod)
        extendedColors.customColor2
    else ColorFamily(
        MaterialTheme.colorScheme.secondaryContainer,
        MaterialTheme.colorScheme.onSecondaryContainer,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.onSecondary,
    )
}

