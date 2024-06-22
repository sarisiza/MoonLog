package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upakon.moonlog.R
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.viewmodel.MoonLogViewModel

@Composable
fun NotesView(
    viewModel: MoonLogViewModel,
    textSize: TextSize,
    trackerEntry: () -> Unit,
    journalEntry: () -> Unit
) {
    val day = viewModel.currentDay.collectAsState().value
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        val note = viewModel.notesState.collectAsState().value[day]
        Row {
            Column(
                modifier = Modifier.weight(1F)
            ) {
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Black
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = day.format(DailyNote.shortFormat),
                            fontSize = textSize.titleSize,
                            fontWeight = FontWeight.Bold
                        )
                        val daysFrom = viewModel.getDaysFromPeriod(day)
                        if(daysFrom <= (viewModel.currentSettings?.cycleDuration ?: 0)){
                            val daysUntil = viewModel.getDaysUntilNextPeriod(day)
                            Text(
                                text = "${stringResource(id = R.string.day)} $daysFrom",
                                fontSize = textSize.textSize
                            )
                            Text(
                                text = "$daysUntil ${getDaysUntilText(daysUntil = daysUntil)}",
                                fontSize = textSize.textSize
                            )
                            val chance = if(viewModel.calculatePregnantChance(daysFrom))
                                stringResource(id = R.string.high)
                            else stringResource(id = R.string.low)
                            Text(
                                text = "${stringResource(id = R.string.pregnant_chances)} $chance",
                                fontSize = textSize.textSize
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clickable { trackerEntry() },
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Black
                    )
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    ) {
                        note?.feeling?.let {
                            item {
                                Text(
                                    text = "${stringResource(id = R.string.i_feel)} ${it.name}",
                                    fontSize = textSize.titleSize,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        //todo trackers
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
                    .padding(4.dp)
                    .clickable { journalEntry() },
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ){
                    Text(
                        text = stringResource(id = R.string.journal),
                        fontSize = textSize.textSize,
                        fontWeight = FontWeight.Bold
                    )
                    note?.journal?.let {
                        Text(
                            text = it,
                            fontSize = textSize.textSize
                        )
                    }
                }
            }
        }
    }
}