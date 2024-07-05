package com.kappdev.wordbook.settings_feature.presentations

import android.net.Uri

sealed class SettingsSheet {
    data object Theme: SettingsSheet()
    data object Language: SettingsSheet()
    data object ProgressBarStyle: SettingsSheet()
    data class ConfirmRestoreBackup(val backupUri: Uri): SettingsSheet()
}