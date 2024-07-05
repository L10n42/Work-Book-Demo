package com.kappdev.wordbook.settings_feature.domain

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brightness4
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.kappdev.wordbook.R

enum class Theme { Light, Dark, SystemDefault }

@Composable
fun Theme.getStringResource(): String = when (this) {
    Theme.Light -> stringResource(R.string.light)
    Theme.Dark -> stringResource(R.string.dark)
    Theme.SystemDefault -> stringResource(R.string.system)
}

@Composable
fun Theme.getIcon(): ImageVector = when (this) {
    Theme.Light -> Icons.Rounded.LightMode
    Theme.Dark -> Icons.Rounded.DarkMode
    Theme.SystemDefault -> Icons.Rounded.Brightness4
}