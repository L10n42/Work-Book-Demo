package com.kappdev.wordbook.main_feature.presentation.collections.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.presentation.common.AnimatedOptionsButton
import com.kappdev.wordbook.core.presentation.common.KeyboardHiddenEffect
import com.kappdev.wordbook.settings_feature.domain.LocalAppSettings

@Composable
fun SearchBox(
    value: String,
    isSearching: Boolean,
    optionsOpened: Boolean,
    modifier: Modifier = Modifier,
    openOptions: () -> Unit,
    onValueChange: (String) -> Unit
) {
    val settings = LocalAppSettings.current
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    val (focusState, changeFocus) = remember { mutableStateOf<FocusState?>(null) }

    KeyboardHiddenEffect {
        focusManager.clearFocus()
    }

    BasicTextField(
        value = value,
        singleLine = true,
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged(changeFocus),
        onValueChange = onValueChange,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 16.sp,
            lineHeight = 18.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = if (settings.capitalizeSentences) KeyboardCapitalization.Sentences else KeyboardCapitalization.None
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            SearchContainer {
                AnimatedContent(
                    targetState = isSearching,
                    modifier = Modifier.padding(8.dp),
                    transitionSpec = {
                        fadeIn(tween(durationMillis = 220, delayMillis = 200)) togetherWith
                                fadeOut(tween(durationMillis = 220))
                    },
                    label = "Searching"
                ) { searching ->
                    when {
                        searching -> SearchProgressIndicator()
                        else -> SearchIcon(focusState?.isFocused ?: false)
                    }
                }

                ContentWithPlaceholder(
                    content = innerTextField,
                    showPlaceholder = value.isEmpty(),
                    modifier = Modifier.weight(1f)
                )

                AnimatedOptionsButton(
                    onClick = openOptions,
                    optionsOpened = optionsOpened
                )
            }
        }
    )
}

@Composable
private fun SearchContainer(
    content: @Composable RowScope.() -> Unit
) {
    Row(
        content = content,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background, CircleShape)
            .border(0.75.dp, MaterialTheme.colorScheme.onBackground.copy(0.25f), CircleShape)
    )
}

@Composable
private fun SearchProgressIndicator() {
    CircularProgressIndicator(
        strokeWidth = 2.dp,
        strokeCap = StrokeCap.Round,
        modifier = Modifier.size(24.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SearchIcon(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = isActive, label = "Search Icon Transition")

    val rotationZ by transition.animateFloat(
        transitionSpec = { tween(400, easing = LinearEasing) },
        label = "Icon Z rotation",
        targetValueByState = { activeState -> if (activeState) 90f else 0f }
    )

    val rotationY by transition.animateFloat(
        transitionSpec = { tween(250, 100, LinearEasing) },
        label = "Icon Y rotation",
        targetValueByState = { activeState -> if (activeState) 360f else 0f }
    )

    Icon(
        imageVector = Icons.Rounded.Search,
        contentDescription = "Search",
        tint = MaterialTheme.colorScheme.onSurface,
        modifier = modifier.graphicsLayer {
            this.rotationZ = rotationZ
            this.rotationY = rotationY
            this.cameraDistance = 5f * density
        }
    )
}

@Composable
private fun ContentWithPlaceholder(
    showPlaceholder: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier) {
        content()
        if (showPlaceholder) {
            Text(
                text = stringResource(R.string.search),
                style = LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    }
}