package com.kappdev.wordbook.core.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kappdev.wordbook.R
import com.kappdev.wordbook.main_feature.domain.model.TermDuplicate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuplicationAlertSheet(
    duplicates: List<TermDuplicate>,
    onDismiss: () -> Unit,
    onCancel: () -> Unit,
    onCreate: () -> Unit
) {
    CustomModalBottomSheet(
        onDismissRequest = onDismiss,
        skipPartiallyExpanded = true,
        dragHandle = null
    ) { triggerDismiss ->
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 16.dp)
        ) {
            AlertTitle(stringResource(R.string.term_duplication))
            VerticalSpace(8.dp)
            AlertMessage(stringResource(R.string.term_duplication_msg))
            VerticalSpace(24.dp)

            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .weight(1f, false)
                    .topFadingEdge(MaterialTheme.colorScheme.surfaceTint, listState.canScrollBackward)
                    .bottomFadingEdge(MaterialTheme.colorScheme.surfaceTint, listState.canScrollForward)
            ) {
                items(duplicates) { duplicate ->
                    DuplicateItem(
                        duplicate = duplicate,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            VerticalSpace(24.dp)
            AlertButtons(
                modifier = Modifier.fillMaxWidth(),
                positiveText = stringResource(R.string.create),
                negativeText = stringResource(R.string.cancel),
                onNegative = { onCancel(); triggerDismiss() },
                onPositive = { onCreate(); triggerDismiss() }
            )
        }
    }
}

@Composable
private fun DuplicateItem(
    duplicate: TermDuplicate,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
    ) {
        DuplicateText(
            title = stringResource(R.string.collection) + ": ",
            text = duplicate.collectionName
        )
        DuplicateText(
            title = stringResource(R.string.term) + ": ",
            text = duplicate.term
        )
        DuplicateText(
            title = stringResource(R.string.definition) + ": ",
            text = duplicate.definition,
            maxLines = 2
        )
    }
}

@Composable
private fun DuplicateText(
    title: String,
    text: String,
    maxLines: Int = 1
) {
    val annotatedString = buildAnnotatedString {
        append(title)
        addStyle(
            style = SpanStyle(fontWeight = FontWeight.SemiBold), start = 0, end = title.length
        )
        append(text)
    }
    Text(
        text = annotatedString,
        maxLines = maxLines,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        color = MaterialTheme.colorScheme.onSurface,
        overflow = TextOverflow.Ellipsis
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertSheet(
    title: String,
    message: String,
    positive: String = stringResource(R.string.ok),
    negative: String = stringResource(R.string.cancel),
    onDismiss: () -> Unit,
    onNegative: () -> Unit = {},
    onPositive: () -> Unit
) {
    CustomModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = null
    ) { triggerDismiss ->
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 16.dp)
        ) {
            AlertTitle(title)
            VerticalSpace(8.dp)
            AlertMessage(message)

            VerticalSpace(24.dp)

            AlertButtons(
                modifier = Modifier.fillMaxWidth(),
                positiveText = positive,
                negativeText = negative,
                onNegative = { onNegative(); triggerDismiss() },
                onPositive = { onPositive(); triggerDismiss() }
            )
        }
    }
}

@Composable
private fun AlertButtons(
    modifier: Modifier = Modifier,
    positiveText: String,
    negativeText: String,
    onNegative: () -> Unit,
    onPositive: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
        modifier = modifier
    ) {
        NegativeButton(negativeText, onClick = onNegative)
        HorizontalSpace(16.dp)
        PositiveButton(positiveText, onClick = onPositive)
    }
}

@Composable
private fun NegativeButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun PositiveButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun AlertMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        maxLines = 5,
        fontSize = 16.sp,
        lineHeight = 18.sp,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.Medium,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun AlertTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        maxLines = 2,
        fontSize = 20.sp,
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface,
        fontWeight = FontWeight.SemiBold,
        overflow = TextOverflow.Ellipsis
    )
}