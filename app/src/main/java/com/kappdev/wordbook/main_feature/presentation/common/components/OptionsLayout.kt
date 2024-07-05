package com.kappdev.wordbook.main_feature.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.main_feature.presentation.common.Option
import com.kappdev.wordbook.main_feature.presentation.common.getTitleString

@Composable
fun OptionsLayout(
    vararg options: Option,
    modifier: Modifier = Modifier,
    onOptionClick: (option: Option) -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        options.forEachIndexed { index, option ->
            OptionItem(
                icon = option.icon,
                title = option.getTitleString(),
                modifier = Modifier.fillMaxWidth(),
                contentColor = when (option) {
                    is Option.Delete ->  MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
                onClick = {
                    onOptionClick(option)
                }
            )

            if (index != options.lastIndex) {
                OptionDivider(
                    Modifier.padding(start = 48.dp, end = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun OptionDivider(
    modifier: Modifier = Modifier
) {
    Divider(
        modifier = modifier.clip(CircleShape),
        color = MaterialTheme.colorScheme.onSurface.copy(0.25f),
        thickness = 0.72.dp
    )
}

@Composable
private fun OptionItem(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Icon(
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onBackground,
            contentDescription = "Option Icon"
        )
        Text(
            text = title,
            maxLines = 1,
            fontSize = 16.sp,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            overflow = TextOverflow.Ellipsis
        )
    }
}