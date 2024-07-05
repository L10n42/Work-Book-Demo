package com.kappdev.wordbook.settings_feature.presentations.components

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Archive
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.NotificationsActive
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material.icons.rounded.ShowChart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Title
import androidx.compose.material.icons.rounded.Upload
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kappdev.wordbook.R
import com.kappdev.wordbook.analytics.domain.repository.LocalAnalyticsHelper
import com.kappdev.wordbook.analytics.domain.util.logBackupAction
import com.kappdev.wordbook.analytics.domain.util.logShare
import com.kappdev.wordbook.core.domain.util.SnackbarStateHandler
import com.kappdev.wordbook.core.presentation.ads.AdManager
import com.kappdev.wordbook.core.presentation.ads.AdUnitId
import com.kappdev.wordbook.core.presentation.common.AlertSheet
import com.kappdev.wordbook.core.presentation.common.LoadingDialog
import com.kappdev.wordbook.core.presentation.common.SimpleTopAppBar
import com.kappdev.wordbook.core.presentation.util.changeAppLocale
import com.kappdev.wordbook.core.presentation.util.getCurrentAppLocale
import com.kappdev.wordbook.core.presentation.util.shareContent
import com.kappdev.wordbook.main_feature.presentation.home_widget.QuickCardWidgetManager
import com.kappdev.wordbook.settings_feature.domain.AppLanguage
import com.kappdev.wordbook.settings_feature.domain.Reminder
import com.kappdev.wordbook.settings_feature.domain.Settings
import com.kappdev.wordbook.settings_feature.domain.Vibration
import com.kappdev.wordbook.settings_feature.domain.getStringResource
import com.kappdev.wordbook.settings_feature.domain.isAllowed
import com.kappdev.wordbook.settings_feature.presentations.SettingsSheet
import com.kappdev.wordbook.settings_feature.presentations.SettingsViewModel


@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val analyticsHelper = LocalAnalyticsHelper.current
    val quickCardWidgetManager = remember { QuickCardWidgetManager(context) }
    val adManager = remember { AdManager(context) }
    val scrollState = rememberScrollState()

    val (sheetState, openSheet) = remember { mutableStateOf<SettingsSheet?>(null) }
    val closeSheet = { openSheet(null) }

    val theme by viewModel.settings.getValueAsState(Settings.Theme)
    val vibration by viewModel.settings.getValueAsState(Settings.Vibration)
    val reminder by viewModel.settings.getValueAsState(Settings.Reminder)
    val progressBarStyle by viewModel.settings.getValueAsState(Settings.ProgressBarStyle)
    val checkForDuplication by viewModel.settings.getValueAsState(Settings.CheckForDuplication)
    val capitalizeSentences by viewModel.settings.getValueAsState(Settings.CapitalizeSentences)

    val snackbarHostState = remember { SnackbarHostState() }
    SnackbarStateHandler(viewModel.snackbarState, snackbarHostState)

    if (adManager.isAdLoading) {
        LoadingDialog()
    }

    LoadingDialog(viewModel.loadingDialog)

    when (sheetState) {
        SettingsSheet.Language -> LanguageChooserSheet(closeSheet) {
            changeAppLocale(it.locale)
            quickCardWidgetManager.updateWidgets()
        }
        SettingsSheet.Theme -> ThemeChooserSheet(theme, closeSheet) { newTheme ->
            viewModel.updateValue(Settings.Theme, newTheme)
        }
        SettingsSheet.ProgressBarStyle -> ProgressBarStyleChooser(progressBarStyle, closeSheet) { newStyle ->
            viewModel.updateValue(Settings.ProgressBarStyle, newStyle)
        }
        is SettingsSheet.ConfirmRestoreBackup -> RestoreBackupConfirmation(onDismiss = closeSheet) {
            viewModel.restoreBackup(sheetState.backupUri) {
                analyticsHelper.logBackupAction("Restored")
                adManager.loadAndShowAd(AdUnitId.RestoreBackup)
            }
        }
        null -> {}
    }

    val collectionImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                viewModel.importCollection(uri) {
                    analyticsHelper.logShare("Collection imported")
                    adManager.loadAndShowAd(AdUnitId.ImportCollection)
                }
            }
        }
    )

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let { openSheet(SettingsSheet.ConfirmRestoreBackup(uri)) }
        }
    )

    Scaffold(
        topBar = {
            SimpleTopAppBar(
                title = stringResource(R.string.settings),
                isElevated = scrollState.canScrollBackward,
                onBack = { navController.popBackStack() }
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { pv ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(pv)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SettingItem(
                title = stringResource(R.string.theme),
                icon = Icons.Rounded.Brush,
                subTitle = theme.getStringResource(),
                onClick = { openSheet(SettingsSheet.Theme) }
            )

            val currentLanguage = getCurrentAppLocale()?.let(AppLanguage::getByLocale)
            SettingItem(
                title = stringResource(R.string.language),
                icon = Icons.Rounded.Language,
                subTitle = currentLanguage?.let { "${it.flag} ${it.nameToDisplay}" },
                onClick = { openSheet(SettingsSheet.Language) }
            )

            SettingsDivider()

            SettingSwitch(
                checked = vibration.isAllowed(),
                title = stringResource(R.string.vibration),
                subTitle = stringResource(R.string.vibration_description),
                icon = Icons.Rounded.Vibration,
                onCheckedChange = {
                    val newValue = if (it) Vibration.Allowed else Vibration.Denied
                    viewModel.updateValue(Settings.Vibration, newValue)
                }
            )

            SettingSwitch(
                checked = (reminder == Reminder.Allowed),
                title = stringResource(R.string.reminders),
                subTitle = stringResource(R.string.reminders_description),
                icon = Icons.Rounded.NotificationsActive,
                onCheckedChange = {
                    val newValue = if (it) Reminder.Allowed else Reminder.Denied
                    viewModel.updateValue(Settings.Reminder, newValue)
                }
            )

            SettingSwitch(
                checked = checkForDuplication,
                title = stringResource(R.string.check_for_duplication),
                subTitle = stringResource(R.string.check_for_duplication_description),
                icon = Icons.Rounded.ContentCopy,
                onCheckedChange = { check ->
                    viewModel.updateValue(Settings.CheckForDuplication, check)
                }
            )

            SettingSwitch(
                checked = capitalizeSentences,
                title = stringResource(R.string.capitalization),
                subTitle = stringResource(R.string.capitalization_description),
                icon = Icons.Rounded.Title,
                onCheckedChange = { capitalize ->
                    viewModel.updateValue(Settings.CapitalizeSentences, capitalize)
                }
            )

            SettingItem(
                title = stringResource(R.string.progress_bar_style),
                icon = Icons.Rounded.ShowChart,
                onClick = { openSheet(SettingsSheet.ProgressBarStyle) }
            )

            SettingsDivider()

            SettingItem(
                title = stringResource(R.string.import_collection),
                icon = Icons.Rounded.Archive,
                onClick = {
                    collectionImportLauncher.launch("application/zip")
                }
            )

            SettingItem(
                title = stringResource(R.string.create_backup),
                icon = Icons.Rounded.Upload,
                onClick = {
                    viewModel.createBackup { backup ->
                        adManager.loadAndShowAd(AdUnitId.CreateBackup) {
                            analyticsHelper.logBackupAction("Created")
                            context.shareContent(backup, type = "application/zip")
                        }
                    }
                }
            )

            SettingItem(
                title = stringResource(R.string.restore_backup),
                icon = Icons.Rounded.Download,
                onClick = {
                    backupLauncher.launch("application/zip")
                }
            )

            SettingsDivider()

            SettingItem(
                title = stringResource(R.string.rate_on_google_play),
                icon = Icons.Rounded.Star,
                onClick = { goToRateTheApp(context) }
            )

            SettingItem(
                title = stringResource(R.string.privacy_policy),
                icon = Icons.Rounded.Security,
                onClick = { openAppPrivacyPolicy(context) }
            )

            SettingItem(
                title = stringResource(R.string.contact_us),
                icon = Icons.Rounded.Email,
                onClick = { openContactUs(context) }
            )
        }
    }
}

@Composable
private fun RestoreBackupConfirmation(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertSheet(
        title = stringResource(R.string.restore_backup),
        message = stringResource(R.string.restore_backup_msg),
        positive = stringResource(R.string.restore),
        onDismiss = onDismiss,
        onPositive = onConfirm
    )
}

@Composable
private fun SettingsDivider() {
    Divider(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(CircleShape),
        color = MaterialTheme.colorScheme.onBackground.copy(0.16f),
        thickness = 0.64.dp
    )
}

private fun openContactUs(context: Context) {
    val emailLink = "mailto:kappdev3@gmail.com?subject=${Uri.encode("WordBook: User Help & Support")}"
    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(emailLink))
    intent.putExtra(Intent.EXTRA_SUBJECT, "")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private fun openAppPrivacyPolicy(context: Context) {
    val webpage: Uri = Uri.parse("https://kappdev3.github.io/apps/wordbook/privacy-policy")
    val intent = Intent(Intent.ACTION_VIEW, webpage)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

private fun goToRateTheApp(context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}