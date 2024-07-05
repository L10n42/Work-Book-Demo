package com.kappdev.wordbook.backup_feature.domain.repository

import android.net.Uri
import com.kappdev.wordbook.backup_feature.domain.util.CreateBackupResult
import com.kappdev.wordbook.backup_feature.domain.util.RestoreBackupResult

interface BackupRepository {

    fun createBackup(): CreateBackupResult

    fun restoreBackup(backupUri: Uri): RestoreBackupResult

}