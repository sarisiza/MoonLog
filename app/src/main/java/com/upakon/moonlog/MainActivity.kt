package com.upakon.moonlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upakon.moonlog.ui.screens.CalendarScreen
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.ui.screens.MoonLogScreens
import com.upakon.moonlog.ui.screens.SettingsPage
import com.upakon.moonlog.ui.theme.MoonLogTheme
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import org.koin.androidx.compose.viewModel

private const val TAG = "MainActivity"
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoonLogTheme {
                // A surface container using the 'background' color from the theme
                val moonLogViewModel: MoonLogViewModel by viewModel()
                val textSize = TextSize()
                val navController = rememberNavController()
                moonLogViewModel.downloadUserSettings()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MoonLogNavGraph(
                        navController = navController,
                        viewModel = moonLogViewModel,
                        modifier = Modifier.padding(16.dp),
                        textSize = textSize)
                }
            }
        }
    }
}

@Composable
fun MoonLogNavGraph(
    navController: NavHostController,
    viewModel: MoonLogViewModel,
    modifier: Modifier,
    textSize: TextSize
){
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = MoonLogScreens.SETTINGS.route
    ){
        composable(MoonLogScreens.SETTINGS.route){
            when(val settingsState = viewModel.userSettings.collectAsState().value){
                is UiState.ERROR -> {
                    Text(text = "Error")
                }
                UiState.LOADING -> {
                    CircularProgressIndicator()
                }
                is UiState.SUCCESS -> {
                    if(settingsState.data.username.isEmpty()){
                        SettingsPage(
                            viewModel,
                            textSize
                        ){
                            navController.navigate(MoonLogScreens.CALENDAR.route)
                        }
                    } else{
                        navController.navigate(MoonLogScreens.CALENDAR.route)
                    }
                }
            }
        }
        composable(MoonLogScreens.CALENDAR.route){
            Column {
                CalendarScreen(
                    viewModel = viewModel,
                    textSize = textSize
                )
            }
        }
    }
}