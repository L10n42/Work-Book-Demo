package com.kappdev.wordbook.share_feature.presentation.card_share.components

import android.os.Build
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindowProvider
import com.kappdev.wordbook.R
import com.kappdev.wordbook.analytics.domain.repository.LocalAnalyticsHelper
import com.kappdev.wordbook.analytics.domain.util.logShare
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.presentation.ads.AdManager
import com.kappdev.wordbook.core.presentation.ads.AdUnitId
import com.kappdev.wordbook.core.presentation.common.HorizontalSpace
import com.kappdev.wordbook.core.presentation.common.LoadingDialog
import com.kappdev.wordbook.core.presentation.util.shareContent
import com.kappdev.wordbook.share_feature.presentation.card_share.CardShareStyle
import com.kappdev.wordbook.share_feature.presentation.card_share.ImageUtils
import com.kappdev.wordbook.share_feature.presentation.common.components.BottomSheetLikeDialog

@Composable
fun ShareCardSheet(
    card: Card,
    onDismiss: () -> Unit
) {
    val analyticsHelper = LocalAnalyticsHelper.current
    val context = LocalContext.current
    val adManager = remember { AdManager(context) }
    var imageBounds by remember { mutableStateOf<Rect?>(null) }

    if (adManager.isAdLoading) {
        LoadingDialog()
    }

    var selectedStyle by remember { mutableStateOf(CardShareStyle.Purple) }
    var showDivider by remember { mutableStateOf(true) }

    BottomSheetLikeDialog(
        onDismiss = onDismiss,
    ) {
        val dialogContext = LocalContext.current
        val view = LocalView.current
        val dialogWindow = (LocalView.current.parent as? DialogWindowProvider)?.window

        ShareCardView(
            card = card,
            style = selectedStyle,
            showDivider = showDivider,
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    imageBounds = when {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> coordinates.boundsInWindow()
                        else -> coordinates.boundsInRoot()
                    }
                }
        )

        ShareStyleChooser(
            selected = selectedStyle,
            modifier = Modifier.padding(vertical = 16.dp),
            onSelect = { selectedStyle = it }
        )

        if (card.image.isNullOrBlank()) {
            OptionCheck(
                checked = showDivider,
                title = stringResource(R.string.show_divider),
                onCheckedChange = { showDivider = it }
            )
        }

        ShareButton(
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 16.dp)
        ) {
            val imageBitmap = imageBounds?.let { ImageUtils.takeSnapshot(view, it, dialogWindow!!) }
            val imageUri = imageBitmap?.let { ImageUtils.saveToCache(it, dialogContext) }
            if (imageUri != null) {
                adManager.loadAndShowAd(AdUnitId.ShareCard) {
                    dialogContext.shareContent(imageUri, type = "image/jpg")
                    analyticsHelper.logShare("Card as image")
                }
            }
        }
    }
}

@Composable
private fun ShareButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = CircleShape,
        modifier = modifier.size(150.dp, 45.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.Share,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        HorizontalSpace(8.dp)
        Text(
            text = stringResource(R.string.share),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun OptionCheck(
    checked: Boolean,
    title: String,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange
                )
                .padding(4.dp)
        )
    }
}