package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.upakon.moonlog.R
import com.upakon.moonlog.notes.DailyNote
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import java.time.LocalDate

@Composable
fun JournalEntryScreen(
    note: DailyNote?,
    textSize: TextSize,
    onDismiss: () -> Unit,
    onClick: (String) -> Unit
){
    val currentEntry = note?.journal
    var journalEntry by remember {
        mutableStateOf(currentEntry ?: "")
    }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    onClick(journalEntry)
                }
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    fontSize = textSize.titleSize
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = note?.day?.format(DailyNote.longFormat) ?: LocalDate.now().format(DailyNote.longFormat),
                    fontSize = textSize.headerSize,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )
                OutlinedTextField(
                    value = journalEntry,
                    onValueChange = {
                        journalEntry = it
                    },
                    textStyle = TextStyle(
                        fontSize = textSize.textSize
                    ),
                    modifier = Modifier
                        .height(500.dp)
                        .fillMaxWidth()
                )
            }
        }
    )
}