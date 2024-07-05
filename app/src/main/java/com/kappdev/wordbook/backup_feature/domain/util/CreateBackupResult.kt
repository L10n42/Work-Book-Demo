package com.kappdev.wordbook.backup_feature.domain.util

import android.net.Uri

sealed class CreateBackupResult {
    data class Success(val uri: Uri): CreateBackupResult()
    data object Error: CreateBackupResult()
    data object EmptyDatabase: CreateBackupResult()
}