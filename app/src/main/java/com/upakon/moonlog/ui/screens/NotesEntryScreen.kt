package com.upakon.moonlog.ui.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.upakon.moonlog.R
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.notes.Tracker
import java.time.LocalDate

private const val TAG = "NotesEntryScreen"

@SuppressLint("MutableCollectionMutableState")
@Composable
fun NotesEntryScreen(
    note: DailyNote?,
    feelings: List<Feeling>,
    trackers: List<Tracker>,
    onDismiss: () -> Unit,
    onConfirm : (DailyNote) -> Unit
){
    var currentFeeling by remember {
        mutableStateOf(note?.feeling)
    }
    val currentTrackers by remember {
        mutableStateOf((note?.notes?.get("trackers") as MutableMap<Tracker,Double>?) ?: mutableMapOf())
    }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    note?.notes?.set("trackers", currentTrackers)
                    val newNote = note?.let {
                        DailyNote(
                            it.day,
                            currentFeeling,
                            it.isPeriod,
                            it.notes,
                            it.journal
                        )
                    } ?: DailyNote(
                        LocalDate.now(),
                        currentFeeling,
                        notes = mutableMapOf("trackers" to currentTrackers)
                    )
                    onConfirm(newNote)
                }
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        },
        text = {
            LazyColumn {
                item {
                    Row {
                        Text(
                            text = stringResource(id = R.string.i_feel),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Start
                        )
                        MoonDropDown(
                            items = feelings,
                            selected = currentFeeling
                        ) {
                            currentFeeling = it
                        }
                    }
                }
                items(trackers){ tracker ->
                    var value by remember {
                        mutableDoubleStateOf(currentTrackers[tracker] ?: 0.0)
                    }
                    Row {
                        Text(
                            text = "${stringResource(id = R.string.bullet)} ${tracker.name}: ",
                            style = MaterialTheme.typography.titleMedium
                        )
                        OutlinedTextField(
                            value = value.toString(),
                            onValueChange = {
                                try {
                                    Log.d(TAG, "NotesEntryScreen: ${it}")
                                    value = it.toDouble()
                                    currentTrackers[tracker] = value
                                }catch (e: Exception){
                                    Log.e(TAG, "NotesEntryScreen: ${e.localizedMessage}", e)
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium,
                            label = {
                                Text(text = tracker.name)
                            }
                        )
                        Text(
                            text = tracker.unit,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    )
}