package com.upakon.moonlog.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class MenuItems(
    val screen: MoonLogScreens,
    val icon: ImageVector
) {
    HOME(MoonLogScreens.HOME, Icons.Default.Home),
    CALENDAR(MoonLogScreens.CALENDAR, Icons.Default.DateRange),
    SETTINGS(MoonLogScreens.SETTINGS,Icons.Default.Settings)
}