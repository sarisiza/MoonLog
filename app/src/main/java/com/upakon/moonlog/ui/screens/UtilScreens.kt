@file:OptIn(ExperimentalMaterial3Api::class)

package com.upakon.moonlog.ui.screens

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.upakon.moonlog.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private const val TAG = "UtilScreens"

@Composable
fun PickDate(
    onDismiss: () -> Unit,
    onConfirm: (LocalDate) -> Unit
){
    val dateState = rememberDatePickerState()
    val date = dateState.selectedDateMillis.toLocalDate()
    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = { onConfirm(date) }) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    ) {
        DatePicker(
            state = dateState,
            showModeToggle = true
        )
    }
}

/**
 * Function to convert EPOCH to LocalDate
 *
 * @return date from epoch milis
 */
fun Long?.toLocalDate(): LocalDate{
    return this?.let {
        Instant
            .ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .plusDays(1)
    } ?: LocalDate.now()
}