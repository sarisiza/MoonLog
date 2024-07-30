@file:OptIn(ExperimentalMaterial3Api::class)

package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.makeappssimple.abhimanyu.composeemojipicker.ComposeEmojiPickerBottomSheetUI
import com.upakon.moonlog.R
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.notes.Tracker
import com.upakon.moonlog.ui.theme.Typography
import com.upakon.moonlog.utils.toLocalDate
import java.time.DayOfWeek
import java.time.LocalDate
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T>MoonDropDown(
    items: List<T>,
    selected: T? = null,
    onSelected: (T) -> Unit
){
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf(selected ?: items[0]) }
    var selectedText by remember { mutableStateOf(getItemText(selectedItem)) }
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
                textStyle = Typography.bodyMedium
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
                                style = Typography.bodyMedium
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

@Composable
fun MoodEditor(
    id : Int,
    onDismiss: () -> Unit,
    onSave: (Feeling) -> Unit
) {
    var emoji by remember {
        mutableStateOf("\uD83D\uDE03")
    }
    var name by remember {
        mutableStateOf("")
    }
    var showEmojiPicker by remember {
        mutableStateOf(false)
    }
    var error by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(
                onClick = {
                    if(emoji.isNotEmpty() && name.isNotEmpty()){
                        val feeling = Feeling(
                            id,
                            name,
                            emoji
                        )
                        onSave(feeling)
                    } else {
                        error = true
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = Typography.bodyMedium
                )
            }
        },
        title = {
            Text(
                text = stringResource(id = R.string.add_mood)
            )
        },
        text = {
            Column {
                Row {
                    Button(
                        onClick = { showEmojiPicker = true }
                    ) {
                        Text(
                            text = emoji
                        )
                    }
                    OutlinedTextField(
                        value = name,
                        onValueChange = {name = it},
                        textStyle = Typography.bodyMedium
                    )
                }
                if(error){
                    Text(
                        text = stringResource(id = R.string.missing_info),
                        style = Typography.bodyMedium,
                        color = Color.Red
                    )
                }
            }
        }
    )
    if(showEmojiPicker) {
        EmojiPickerDialog(
            onDismiss = { showEmojiPicker = false }
        ) {
            emoji = it
            showEmojiPicker = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmojiPickerDialog(
    onDismiss: () -> Unit,
    onSelected : (String) -> Unit
){
    BasicAlertDialog(
        onDismissRequest = { onDismiss() }
    ) {
        var searchText by remember {
            mutableStateOf("")
        }
        ComposeEmojiPickerBottomSheetUI(
            onEmojiClick = {
                onSelected(it.character)
            },
            searchText = searchText,
            updateSearchText = {
                searchText = it
            }
        )
    }
}

@Composable
fun TrackerEditor(
    onDismiss: () -> Unit,
    onSave: (Tracker) -> Unit
){
    var name by remember {
        mutableStateOf("")
    }
    var unit by remember {
        mutableStateOf("")
    }
    var showError by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            Button(onClick = {
                if(name.isEmpty() || unit.isEmpty()){
                    showError = true
                } else{
                    onSave(Tracker(name,unit))
                }
            }) {
                Text(
                    text = stringResource(id = R.string.save),
                    style = Typography.bodyMedium
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text(
                        text = stringResource(id = R.string.tracker_name),
                        style = Typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    OutlinedTextField(
                        value = name,
                        onValueChange = {name = it},
                        textStyle = Typography.bodyMedium
                    )
                }
                Row {
                    Text(
                        text = stringResource(id = R.string.unit_name),
                        style = Typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {unit = it},
                        textStyle = Typography.bodyMedium
                    )
                }
            }
        }
    )
}

fun <T>getItemText(item: T): String {
    return when(item){
        is DayOfWeek -> item.getDisplayName(TextStyle.FULL, Locale.getDefault())
        is Feeling -> "${item.emoji} ${item.name}"
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


