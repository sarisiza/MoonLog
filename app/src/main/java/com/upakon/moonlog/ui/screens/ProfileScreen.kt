package com.upakon.moonlog.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.emoji2.emojipicker.EmojiViewItem
import com.makeappssimple.abhimanyu.composeemojipicker.ComposeEmojiPickerBottomSheetUI
import com.makeappssimple.abhimanyu.composeemojipicker.Emoji
import com.upakon.moonlog.R
import com.upakon.moonlog.notes.Feeling
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.utils.getNextId
import com.upakon.moonlog.viewmodel.MoonLogViewModel

@Composable
fun ProfileScreen(
    viewModel: MoonLogViewModel,
    textSize: TextSize
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        val currentSettings = viewModel.currentSettings
        var feelingId = 0
        var trackerId = 0
        currentSettings?.let { settings ->
            var showMoodEditor by remember{
                mutableStateOf(false)
            }
            var showTrackerEditor by remember {
                mutableStateOf(false)
            }
            Text(
                text = "${stringResource(id = R.string.welcome_short)} ${settings.username}!",
                fontSize = textSize.headerSize,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(5.dp)
            )
            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight(0.5f)
                    .fillMaxWidth()
            ) {
                val moodsState = viewModel.feelingsList.collectAsState().value
                Text(
                    text = stringResource(id = R.string.my_moods),
                    fontSize = textSize.titleSize
                )
                LazyColumn(
                  modifier = Modifier.padding(10.dp)
                ) {
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
                            feelingId = moodsState.data.getNextId()
                            items(moodsState.data){
                                Text(
                                    text = "${it.emoji} ${it.name}",
                                    fontSize = textSize.textSize
                                )
                            }
                        }
                    }
                }
                IconButton(
                    onClick = { showMoodEditor = true },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = stringResource(id = R.string.add_mood)
                        )
                        Text(
                            text = stringResource(id = R.string.add_mood),
                            fontSize = textSize.titleSize
                        )
                    }
                }
                
            }
            Card(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.my_trackers),
                    fontSize = textSize.titleSize
                )
                LazyColumn(
                    modifier = Modifier.padding(10.dp)
                ) {
                    //todo trackers list
                }
                IconButton(
                    onClick = { showTrackerEditor = false },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = stringResource(id = R.string.add_tracker)
                        )
                        Text(
                            text = stringResource(id = R.string.add_tracker),
                            fontSize = textSize.titleSize
                        )
                    }
                }
            }
            if(showMoodEditor){
                MoodEditor(
                    id = feelingId,
                    textSize = textSize,
                    onDismiss = {showMoodEditor = false}
                ) {
                    viewModel.addFeeling(it)
                    viewModel.getFeelings()
                    showMoodEditor = false
                }
            }
            if(showTrackerEditor){

            }
        } ?: run {
            //todo handle null user
            //shouldn't get here, but just in case
        }
    }
}

@Composable
fun MoodEditor(
    id : Int,
    textSize: TextSize,
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
                    fontSize = textSize.textSize
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
                        textStyle = TextStyle(
                            fontSize = textSize.textSize
                        )
                    )
                }
                if(error){
                    Text(
                        text = stringResource(id = R.string.missing_info),
                        fontSize = textSize.textSize,
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