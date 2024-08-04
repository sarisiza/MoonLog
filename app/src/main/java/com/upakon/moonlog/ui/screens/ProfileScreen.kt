package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.upakon.moonlog.R
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.utils.getNextId
import com.upakon.moonlog.viewmodel.MoonLogViewModel

@Composable
fun ProfileScreen(
    viewModel: MoonLogViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        val currentSettings = viewModel.currentSettings
        var feelingId = 0
        var trackerId = 0
        var message by remember {
            mutableStateOf("")
        }
        var showError by remember {
            mutableStateOf(false)
        }
        currentSettings?.let { settings ->
            var showMoodEditor by remember{
                mutableStateOf(false)
            }
            var showTrackerEditor by remember {
                mutableStateOf(false)
            }
            Text(
                text = "${stringResource(id = R.string.welcome_short)} ${settings.username}!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
            )
            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth(),
                colors = CardColors(
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.onTertiaryContainer,
                    MaterialTheme.colorScheme.surfaceDim,
                    MaterialTheme.colorScheme.onSurface
                )
            ) {
                val moodsState = viewModel.feelingsList.collectAsState().value
                Text(
                    text = stringResource(id = R.string.my_moods),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                LazyColumn(
                  modifier = Modifier
                      .padding(10.dp)
                      .fillMaxHeight(0.8f)
                ) {
                    when (moodsState){
                        is UiState.ERROR -> {
                            //todo add analytics
                            item{
                                message = stringResource(id = R.string.moods_error)
                                showError = true
                            }
                        }
                        UiState.LOADING ->{
                            item {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.SUCCESS -> {
                            feelingId = moodsState.data.getNextId()
                            items(moodsState.data){
                                Text(
                                    text = "${it.emoji} ${it.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = { showMoodEditor = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = stringResource(id = R.string.add_mood)
                        )
                        Text(
                            text = stringResource(id = R.string.add_mood),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                
            }
            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(),
                colors = CardColors(
                    MaterialTheme.colorScheme.tertiaryContainer,
                    MaterialTheme.colorScheme.onTertiaryContainer,
                    MaterialTheme.colorScheme.surfaceDim,
                    MaterialTheme.colorScheme.onSurface
                )
            ) {
                val trackersState = viewModel.trackersList.collectAsState().value
                Text(
                    text = stringResource(id = R.string.my_trackers),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxHeight(0.8f)
                ) {
                    when (trackersState) {
                        is UiState.ERROR -> {
                            //todo add analytics
                            item{
                                message = stringResource(id = R.string.trackers_error)
                                showError = true
                            }
                        }
                        UiState.LOADING -> {
                            item {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.SUCCESS -> {
                            items(trackersState.data){
                                Text(
                                    text = "${stringResource(id = R.string.bullet)} ${it.name}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = { showTrackerEditor = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = stringResource(id = R.string.add_tracker)
                        )
                        Text(
                            text = stringResource(id = R.string.add_tracker),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
            if(showMoodEditor){
                MoodEditor(
                    id = feelingId,
                    onDismiss = {showMoodEditor = false}
                ) {
                    viewModel.addFeeling(it)
                    viewModel.getFeelings()
                    showMoodEditor = false
                }
            }
            if(showTrackerEditor){
                TrackerEditor(
                    onDismiss = { showTrackerEditor = false }
                ) {
                    viewModel.addTracker(it)
                    viewModel.getTrackers()
                    showTrackerEditor = false
                }
            }
        } ?: run {
            //todo add analytics
            message = stringResource(id = R.string.settings_error)
            showError = true
        }
        if(showError){
            ErrorMessage(
                message = message,
                onDismiss = { showError = false }
            )
        }
    }
}