package com.upakon.moonlog.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Charcoal, //texts
    secondary = MutedNavy, //titles
    tertiary = LightGray, //navigation texts
    primaryContainer = RoyalPurple, //navigation
    onPrimaryContainer = RoyalPurple700, //on navigation
    secondaryContainer = SoftRed, //period day
    onSecondaryContainer = SoftRed400, //on period day
    tertiaryContainer = PalePink, //prediction day
    onTertiaryContainer = PalePink300, //on prediction day
    surfaceContainerHigh = YellowSunshine, //post its
    onSurface = YellowSunshine600, //on post it
    surfaceContainerLow = BrightMint, //main card
    background = LightLavender, //background
    surfaceVariant = WarmCoral, //missing days card
    inversePrimary = SoftTeal, //days
    onSurfaceVariant = SoftTeal700 //on day
)

private val LightColorScheme = lightColorScheme(
    primary = Charcoal, //texts
    secondary = MutedNavy, //titles
    tertiary = LightGray, //navigation texts
    primaryContainer = RoyalPurple, //navigation
    onPrimaryContainer = RoyalPurple700, //on navigation
    secondaryContainer = SoftRed, //period day
    onSecondaryContainer = SoftRed400, //on period day
    tertiaryContainer = PalePink, //prediction day
    onTertiaryContainer = PalePink300, //on prediction day
    surfaceContainerHigh = YellowSunshine, //post its
    onSurface = YellowSunshine600, //on post it
    surfaceContainerLow = BrightMint, //main card
    background = LightLavender, //background
    surfaceVariant = WarmCoral, //missing days card
    inversePrimary = SoftTeal, //days
    onSurfaceVariant = SoftTeal700 //on day

    /* Other default colors to override
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun MoonLogTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}