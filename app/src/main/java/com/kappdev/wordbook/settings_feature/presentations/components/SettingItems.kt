package com.kappdev.wordbook.settings_feature.presentations.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.core.presentation.common.AutoMirroredIcon


@Composable
fun SettingsGroup(
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface, ItemShape)
            .padding(horizontal = 8.dp),
        content = content
    )
}

@Composable
fun SettingSwitch(
    checked: Boolean,
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    subTitle: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = modifier
            .height(ItemHeight)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, ItemShape)
            .padding(start = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ItemIcon(icon = icon)

        Column(Modifier.weight(1f)) {
            ItemTitle(text = title)
            if (subTitle != null) {
                ItemSubTitle(subTitle)
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.onSurface,
            )
        )
    }
}

@Composable
fun SettingItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    subTitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .height(ItemHeight)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, ItemShape)
            .clip(ItemShape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(start = 8.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ItemIcon(
            icon = icon,
            enabled = enabled
        )

        Column(Modifier.weight(1f)) {
            ItemTitle(
                text = title,
                enabled = enabled
            )
            if (subTitle != null) {
                ItemSubTitle(subTitle)
            }
        }

        AutoMirroredIcon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null
        )
    }
}

@Composable
private fun ItemIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Icon(
        imageVector = icon,
        modifier = modifier,
        contentDescription = null,
        tint = when {
            enabled -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onBackground
        }
    )
}

@Composable
private fun ItemTitle(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Text(
        text = text,
        maxLines = 1,
        fontSize = 16.sp,
        modifier = modifier,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis,
        color = when {
            enabled -> MaterialTheme.colorScheme.onSurface
            else -> MaterialTheme.colorScheme.onBackground
        }
    )
}

@Composable
private fun ItemSubTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        maxLines = 1,
        fontSize = 12.sp,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        color = MaterialTheme.colorScheme.onBackground
    )
}

private val ItemShape = RoundedCornerShape(8.dp)
private val ItemHeight = 50.dp