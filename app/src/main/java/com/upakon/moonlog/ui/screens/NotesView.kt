package com.upakon.moonlog.ui.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upakon.moonlog.R
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Tracker
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import com.upakon.moonlog.viewmodel.NoteAction
import com.upakon.moonlog.viewmodel.NoteType

private const val TAG = "NotesView"
@Composable
fun NotesView(
    viewModel: MoonLogViewModel,
    onEmpty: () -> Unit
) {
    val day = viewModel.currentDay.collectAsState().value
    val note = viewModel.notesState.collectAsState().value[day]
    var showNotesEdit by remember {
        mutableStateOf(false)
    }
    var showJournalEntry by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
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
                        color = MaterialTheme.colorScheme.inverseSurface
                    ),
                    colors = CardColors(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onTertiaryContainer,
                        MaterialTheme.colorScheme.surfaceDim,
                        MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = day.format(DailyNote.shortFormat),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        val daysFrom = viewModel.getDaysFromPeriod(day)
                        if(daysFrom <= (viewModel.currentSettings?.cycleDuration ?: 0)){
                            val daysUntil = viewModel.getDaysUntilNextPeriod(day)
                            Text(
                                text = "${stringResource(id = R.string.day)} $daysFrom",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "$daysUntil ${getDaysUntilText(daysUntil = daysUntil)}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            val chance = if(viewModel.calculatePregnantChance(daysFrom))
                                stringResource(id = R.string.high)
                            else stringResource(id = R.string.low)
                            Text(
                                text = "${stringResource(id = R.string.pregnant_chances)} $chance",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .clickable { showNotesEdit = true },
                    border = BorderStroke(
                        width = 1.dp,
                        color = Color.Black
                    ),
                    colors = CardColors(
                        MaterialTheme.colorScheme.tertiaryContainer,
                        MaterialTheme.colorScheme.onTertiaryContainer,
                        MaterialTheme.colorScheme.surfaceDim,
                        MaterialTheme.colorScheme.onSurface
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
                                    text = "${stringResource(id = R.string.i_feel)} ${it.emoji} ${it.name}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        item {
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                        note?.notes?.get("trackers")?.let { trackMap ->
                            Log.d(TAG, "NotesView: ${trackMap}")
                            val trackers = (trackMap as MutableMap<Tracker,Double>).map { track ->
                                "${track.key.name}: ${track.value} ${track.key.unit}"
                            }
                            items(trackers){
                                Text(
                                    text = "${stringResource(id = R.string.bullet)} $it",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
            Card(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
                    .padding(4.dp)
                    .clickable { showJournalEntry = true },
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.Black
                ),
                colors = CardColors(
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.onTertiaryContainer,
                    MaterialTheme.colorScheme.surfaceDim,
                    MaterialTheme.colorScheme.onSurface
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
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    note?.journal?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
    if(showNotesEdit){
        val feelingsState = viewModel.feelingsList.collectAsState().value
        val trackersState = viewModel.trackersList.collectAsState().value
        if (feelingsState is UiState.SUCCESS && trackersState is UiState.SUCCESS){
            if(feelingsState.data.isEmpty() && trackersState.data.isEmpty()){
                onEmpty()
            } else{
                NotesEntryScreen(
                    note = note,
                    feelings = feelingsState.data,
                    trackers = trackersState.data,
                    onDismiss = { showNotesEdit = false }
                ){
                    if(it.feeling != null){
                        viewModel.sendNoteEvent(NoteType.MOOD,NoteAction.ADD)
                    }
                    if((it.notes["trackers"] as Map<Tracker,Double>?)?.isNotEmpty() == true){
                        viewModel.sendNoteEvent(NoteType.TRACKER,NoteAction.ADD)
                    }
                    viewModel.saveDailyNote(it)
                    showNotesEdit = false
                }
            }
        }
    }
    if(showJournalEntry){
        JournalEntryScreen(
            note = note,
            onDismiss = { showJournalEntry = false }
        ) {entry ->
            val newNote = note?.let { dailyNote ->
                 DailyNote(
                    dailyNote.day,
                    dailyNote.feeling,
                    dailyNote.isPeriod,
                    dailyNote.notes,
                    entry
                )
            } ?: DailyNote(
                day,
                journal = entry
            )
            viewModel.sendNoteEvent(NoteType.JOURNAL,NoteAction.ADD)
            viewModel.saveDailyNote(newNote)
            showJournalEntry = false
        }
    }
}