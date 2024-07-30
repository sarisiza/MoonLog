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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.upakon.moonlog.ui.screens.CalendarScreen
import com.upakon.moonlog.ui.screens.HomePage
import com.upakon.moonlog.ui.screens.JournalEntryScreen
import com.upakon.moonlog.ui.screens.MenuItems
import com.upakon.moonlog.utils.UiState
import com.upakon.moonlog.ui.screens.MoonLogScreens
import com.upakon.moonlog.ui.screens.ProfileScreen
import com.upakon.moonlog.ui.screens.SettingsPage
import com.upakon.moonlog.ui.theme.MoonLogTheme
import com.upakon.moonlog.viewmodel.MoonLogViewModel
import org.koin.androidx.compose.viewModel
import java.util.Locale

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(Locale.getDefault().language)
        )
        setContent {
            MoonLogTheme(
                dynamicColor = false
            ) {
                // A surface container using the 'background' color from the theme
                val moonLogViewModel: MoonLogViewModel by viewModel()
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
                    },
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    stringResource(id = R.string.app_name),
                                    style = MaterialTheme.typography.headlineLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            colors = TopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.tertiary,
                                scrolledContainerColor = MaterialTheme.colorScheme.tertiary,
                                actionIconContentColor = MaterialTheme.colorScheme.tertiary,
                                navigationIconContentColor = MaterialTheme.colorScheme.tertiary,
                            )
                        )
                    }
                ) {pad ->
                    MoonLogNavGraph(
                        navController = navController,
                        viewModel = moonLogViewModel,
                        modifier = Modifier.padding(pad)
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
    modifier: Modifier
){
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = MoonLogScreens.LOGIN.route
    ){
        composable(MoonLogScreens.LOGIN.route){
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
                            viewModel
                        ){
                            viewModel.downloadUserSettings()
                            navController.navigate(MoonLogScreens.HOME.route)
                        }
                    } else{
                        navController.navigate(MoonLogScreens.HOME.route)
                    }
                }
            }
        }
        composable(MoonLogScreens.HOME.route){
            viewModel.getMonthlyNotes()
            viewModel.goToToday()
            viewModel.getFeelings()
            viewModel.getTrackers()
            HomePage(
                viewModel = viewModel
            )
        }
        composable(MoonLogScreens.CALENDAR.route){
            Column {
                CalendarScreen(
                    viewModel = viewModel
                )
            }
        }
        composable(MoonLogScreens.SETTINGS.route){
            SettingsPage(
                viewModel
            ){
                viewModel.downloadUserSettings()
                navController.navigate(MoonLogScreens.HOME.route)
            }
        }
        composable(MoonLogScreens.PROFILE.route){
            ProfileScreen(
                viewModel = viewModel)
        }
    }
}

@Composable
fun BottomMenu(
    navController: NavHostController
){
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.tertiary
    ) {
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