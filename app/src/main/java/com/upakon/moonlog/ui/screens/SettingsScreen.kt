package com.upakon.moonlog.ui.screens

import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.upakon.moonlog.R
import com.upakon.moonlog.settings.UserSettings
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private const val TAG = "SettingsScreen"
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SettingsPage(
    viewModel: MoonLogViewModel,
    textSize: TextSize,
    navigate: () -> Unit
){
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    var username by remember{ mutableStateOf("") }
    var periodDate = LocalDate.now()
    var lastPeriodString by remember{ mutableStateOf(periodDate.format(formatter)) }
    var periodDuration by remember { mutableStateOf("0") }
    var cycleDuration by remember { mutableStateOf("0") }
    var showDatePicker by remember {mutableStateOf(false)}
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ){
        val focusManager = LocalFocusManager.current
        Text(
            text = stringResource(R.string.welcome),
            fontSize = textSize.headerSize,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.size(12.dp))
        Row {
            Text(
                text = stringResource(id = R.string.username),
                fontSize = textSize.textSize,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                },
                label = { Text(text = stringResource(id = R.string.username))},
                enabled = true,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = textSize.textSize
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    autoCorrect = false
                ),
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.moveFocus(FocusDirection.Down)}
                )
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Row {
            Text(
                text = stringResource(id = R.string.last_period),
                fontSize = textSize.textSize,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = lastPeriodString,
                onValueChange = {
                    lastPeriodString = it
                },
                label = { Text(text = stringResource(id = R.string.last_period))},
                enabled = false,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = textSize.textSize
                ),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_calendar_month_24),
                            contentDescription = null
                        )
                    }
                }
            )
        }
        if(showDatePicker){
            PickDate(onDismiss = { showDatePicker = false }) {
                periodDate = it
                lastPeriodString = it.format(formatter)
                showDatePicker = false
            }
        }
        Spacer(modifier = Modifier.size(12.dp))
        Row {
            Text(
                text = stringResource(id = R.string.period_duration),
                fontSize = textSize.textSize,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = periodDuration,
                onValueChange = {
                    periodDuration = it
                },
                label = { Text(text = stringResource(id = R.string.period_duration))},
                enabled = true,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = textSize.textSize
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.moveFocus(FocusDirection.Down)}
                )
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Row {
            Text(
                text = stringResource(id = R.string.cycle_duration),
                fontSize = textSize.textSize,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = cycleDuration,
                onValueChange = {
                    cycleDuration = it
                },
                label = { Text(text = stringResource(id = R.string.cycle_duration))},
                enabled = true,
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = textSize.textSize
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onNext = {focusManager.moveFocus(FocusDirection.Down)}
                )
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Row {
            Button(
                onClick = {
                    try {
                        val user = UserSettings(
                            username,
                            periodDate,
                            periodDuration.toInt(),
                            cycleDuration.toInt(),
                        )
                        viewModel.saveUserSettings(user)
                        navigate()
                    }catch (e: Exception){
                        Log.e(TAG, "SettingsPage: ${e.localizedMessage}", )
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    fontSize = textSize.textSize
                )
            }
        }
    }
}
