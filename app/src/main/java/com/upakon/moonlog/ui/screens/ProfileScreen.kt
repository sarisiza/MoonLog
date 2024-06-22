package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.upakon.moonlog.R
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.viewmodel.MoonLogViewModel

@Composable
fun ProfileScreen(
    viewModel: MoonLogViewModel,
    textSize: TextSize,
    onMoodsClick: () -> Unit,
    onTrackersClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        val currentSettings = viewModel.currentSettings
        currentSettings?.let { settings ->
            Text(
                text = "${stringResource(id = R.string.welcome_short)} ${settings.username}!",
                fontSize = textSize.headerSize,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
            )
            Card(
                onClick = { onMoodsClick() },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth()
            ) {
                val moodsState = viewModel.feelingsList.collectAsState().value
                LazyColumn(
                  modifier = Modifier.padding(10.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.my_moods),
                            fontSize = textSize.titleSize
                        )
                    }
                    when (moodsState){
                        is UiState.ERROR -> {
                            //todo handle error
                        }
                        UiState.LOADING ->{
                            item {
                                CircularProgressIndicator()
                            }
                        }
                        is UiState.SUCCESS -> {
                            items(moodsState.data){
                                Text(text = "${it.emoji} ${it.name}")
                            }
                        }
                    }
                }
            }
            Card(
                onClick = { onTrackersClick() },
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                LazyColumn(
                    modifier = Modifier.padding(10.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(id = R.string.my_trackers),
                            fontSize = textSize.titleSize
                        )
                    }
                    //todo trackers list
                }
            }
        } ?: run {
            //todo handle null user
            //shouldn't get here, but just in case
        }
    }
}