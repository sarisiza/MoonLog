package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
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
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import java.time.LocalDate
import kotlin.math.abs

@Composable
fun HomePage(
    viewModel: MoonLogViewModel,
    textSize: TextSize
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        when(val settings = viewModel.userSettings.collectAsState().value){
            is UiState.ERROR -> {
                //todo show error
            }
            UiState.LOADING -> {
                CircularProgressIndicator()
            }
            is UiState.SUCCESS -> {
                var showDatePicker by remember {
                    mutableStateOf(false)
                }
                val today = LocalDate.now()
                Text(
                    text = "${stringResource(id = R.string.welcome_short)} ${settings.data.username}!",
                    fontSize = textSize.headerSize,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                )
                Text(
                    text = "${stringResource(id = R.string.today_is)} ${today.format(DailyNote.longFormat)}",
                    fontSize = textSize.titleSize,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(5.dp)
                )
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                ){
                    Row(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        val daysUntil = viewModel.getDaysUntilNextPeriod(today)
                        val daysText = getDaysUntilText(daysUntil = daysUntil)
                        Column(
                            modifier = Modifier
                                .padding(10.dp)
                                .weight(1F)
                        ) {
                            Card(
                                colors = CardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    contentColor = Color.White,
                                    disabledContentColor = Color.Transparent,
                                    disabledContainerColor = Color.White
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.surfaceContainerLow
                                ),
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                            ) {
                                Text(
                                    text = "${abs(daysUntil)}",
                                    fontSize = textSize.headerSize,
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .align(Alignment.CenterHorizontally)
                                )
                            }
                            Text(
                                text = daysText,
                                fontSize = textSize.titleSize,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                        Button(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(10.dp)
                                .weight(1F),
                            onClick = {
                                showDatePicker = true
                            }
                        ) {
                            Text(
                                text = stringResource(id = R.string.start_period),
                                fontSize = textSize.headerSize
                            )
                        }
                    }
                }
                NotesView(
                    viewModel = viewModel,
                    textSize = textSize
                )
                if(showDatePicker){
                    PickDate(onDismiss = { showDatePicker = false }) { date ->
                        if(!date.isAfter(LocalDate.now()))
                            viewModel.updateLatestPeriod(date)
                        showDatePicker = false
                    }
                }
            }
        }
    }
}