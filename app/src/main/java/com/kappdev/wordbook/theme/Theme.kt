package com.kappdev.wordbook.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.kappdev.wordbook.settings_feature.domain.Theme

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,

    background = Charcoal,
    onBackground = MediumGray,

    surface = DarkCharcoal,
    onSurface = LightGray,

    surfaceVariant = Charcoal,
    surfaceTint = MidnightBlack
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBlue,
    onPrimary = Color.White,

    background = LinenWhite,
    onBackground = SilverSlate,

    surface = Color.White,
    onSurface = Graphite,

    surfaceVariant = Color.White,
    surfaceTint = Color.White
)

val LocalAppTheme = staticCompositionLocalOf { Theme.SystemDefault }

@Composable
fun WordBookTheme(
    theme: Theme,
    content: @Composable () -> Unit
) {
    val darkTheme = when(theme) {
        Theme.Dark -> true
        Theme.Light -> false
        Theme.SystemDefault -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(colorScheme.surface)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography
    ) {
        CompositionLocalProvider(
            LocalAppTheme provides theme
        ) {
            content()
        }
    }
}