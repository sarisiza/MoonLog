package com.upakon.moonlog

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.upakon.moonlog.ui.screens.CalendarScreen
import com.upakon.moonlog.ui.screens.HomePage
import com.upakon.moonlog.ui.screens.MenuItems
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.ui.screens.MoonLogScreens
import com.upakon.moonlog.ui.screens.SettingsPage
import com.upakon.moonlog.ui.theme.MoonLogTheme
import com.upakon.moonlog.ui.theme.TextSize
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import org.koin.androidx.compose.viewModel
import java.util.Locale

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(Locale.getDefault().language)
        )
        setContent {
            MoonLogTheme {
                // A surface container using the 'background' color from the theme
                val moonLogViewModel: MoonLogViewModel by viewModel()
                val textSize = TextSize()
                val navController = rememberNavController()
                moonLogViewModel.downloadUserSettings()
                var userSettingsState by remember {
                    mutableStateOf(false)
                }
                val settings = moonLogViewModel.userSettings.collectAsState().value
                if(settings is UiState.SUCCESS && !settings.data.username.isNullOrEmpty()){
                    userSettingsState = true
                }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if(userSettingsState){
                            BottomMenu(navController = navController)
                        }
                    }
                ) {pad ->
                    MoonLogNavGraph(
                        navController = navController,
                        viewModel = moonLogViewModel,
                        modifier = Modifier.padding(pad),
                        textSize = textSize
                    )
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
                    Log.e(TAG, "MoonLogNavGraph: ${settingsState.error}", settingsState.error)
                    Text(text = "Error: ${settingsState.error.localizedMessage}")
                }
                UiState.LOADING -> {
                    CircularProgressIndicator()
                }
                is UiState.SUCCESS -> {
                    if(settingsState.data.username == null){
                        SettingsPage(
                            viewModel,
                            textSize
                        ){
                            navController.navigate(MoonLogScreens.HOME.route)
                        }
                    } else{
                        navController.navigate(MoonLogScreens.HOME.route)
                    }
                }
            }
        }
        composable(MoonLogScreens.HOME.route){
            HomePage(
                viewModel = viewModel,
                textSize = textSize
            )
        }
        composable(MoonLogScreens.CALENDAR.route){
            viewModel.getMonthlyNotes()
            Column {
                CalendarScreen(
                    viewModel = viewModel,
                    textSize = textSize
                )
            }
        }
    }
}

@Composable
fun BottomMenu(
    navController: NavHostController
){
    BottomAppBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        MenuItems.entries.forEach { item ->
            IconButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                enabled = currentRoute != item.screen.route,
                onClick = { navController.navigate(item.screen.route){
                    popUpTo(navController.graph.startDestinationId)
                    launchSingleTop = true
                } }
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.name
                )
            }
        }

    }
}