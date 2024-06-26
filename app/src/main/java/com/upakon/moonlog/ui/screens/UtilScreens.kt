@file:OptIn(ExperimentalMaterial3Api::class)

package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.upakon.moonlog.R
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.utils.toLocalDate
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

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

@Composable
fun <T>MoonDropDown(
    items: List<T>,
    textSize: TextUnit,
    selected: T? = null,
    onSelected: (T) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var selectedItem by remember { mutableStateOf(selected?:items[0]) }
    var selectedText by remember { mutableStateOf(getItemText(selected)) }
    Box(
        modifier = Modifier
            .padding(20.dp)
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {expanded = !expanded}
        ) {
            OutlinedTextField(
                value = selectedText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontSize = textSize
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = getItemText(item),
                                fontSize = textSize
                            ) },
                        onClick = {
                            selectedItem = item
                            selectedText = getItemText(item)
                            onSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }

    }

}

fun <T>getItemText(item: T): String {
    return when(item){
        is DayOfWeek -> item.getDisplayName(TextStyle.FULL, Locale.getDefault())
        is String -> item
        else -> item.toString()
    }
}

@Composable
fun getDaysUntilText(daysUntil : Int) : String {
    return if(daysUntil >= 0)
        stringResource(id = R.string.days_until)
    else
        stringResource(id = R.string.days_late)
}


