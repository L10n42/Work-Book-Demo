package com.kappdev.wordbook.settings_feature.presentations

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappdev.wordbook.R
import com.kappdev.wordbook.backup_feature.domain.repository.BackupRepository
import com.kappdev.wordbook.backup_feature.domain.util.CreateBackupResult.EmptyDatabase
import com.kappdev.wordbook.backup_feature.domain.util.CreateBackupResult.Error
import com.kappdev.wordbook.backup_feature.domain.util.CreateBackupResult.Success
import com.kappdev.wordbook.backup_feature.domain.util.RestoreBackupResult
import com.kappdev.wordbook.core.domain.util.DialogState
import com.kappdev.wordbook.core.domain.util.SnackbarState
import com.kappdev.wordbook.core.domain.util.mutableDialogStateOf
import com.kappdev.wordbook.settings_feature.domain.Settings
import com.kappdev.wordbook.settings_feature.domain.repository.SettingsRepository
import com.kappdev.wordbook.share_feature.domain.repository.ShareCollection
import com.kappdev.wordbook.share_feature.domain.util.ImportCollectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val settings: SettingsRepository,
    private val backupRepository: BackupRepository,
    private val shareCollection: ShareCollection
) : ViewModel() {

    val snackbarState = SnackbarState(viewModelScope)

    private var _loadingDialog = mutableDialogStateOf<Int?>(null)
    val loadingDialog: DialogState<Int?> = _loadingDialog

    fun importCollection(zipUri: Uri, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingDialog.showDialog(R.string.importing)
            val importResult = shareCollection.importCollection(zipUri)
            _loadingDialog.hideDialog()

            when (importResult) {
                ImportCollectionResult.FileError -> snackbarState.show(R.string.get_file_error)
                ImportCollectionResult.WrongFile -> snackbarState.show(R.string.not_supported_file_error)
                ImportCollectionResult.DataError -> snackbarState.show(R.string.import_data_error)
                ImportCollectionResult.Success -> withContext(Dispatchers.Main) {
                    onSuccess()
                    snackbarState.show(R.string.successfully_imported)
                }
            }
        }
    }

    fun createBackup(onSuccess: (Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingDialog.showDialog(R.string.creating_backup)
            val backupResult = backupRepository.createBackup()
            _loadingDialog.hideDialog()

            when (backupResult) {
                Error -> snackbarState.show(R.string.create_backup_error)
                EmptyDatabase -> snackbarState.show(R.string.empty_database_error)
                is Success -> withContext(Dispatchers.Main) { onSuccess(backupResult.uri) }
            }
        }
    }

    fun restoreBackup(backupUri: Uri, onSuccess: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingDialog.showDialog(R.string.restoring_backup)
            val backupResult = backupRepository.restoreBackup(backupUri)
            _loadingDialog.hideDialog()

            when (backupResult) {
                RestoreBackupResult.FileError -> snackbarState.show(R.string.get_file_error)
                RestoreBackupResult.WrongFile -> snackbarState.show(R.string.not_supported_file_error)
                RestoreBackupResult.DbError -> snackbarState.show(R.string.restore_backup_error)
                RestoreBackupResult.Success -> withContext(Dispatchers.Main) {
                    onSuccess()
                    snackbarState.show(R.string.successfully_restored)
                }
            }
        }
    }

    fun <V, P> updateValue(settings: Settings<V, P>, newValue: V) {
        viewModelScope.launch {
            this@SettingsViewModel.settings.setValueTo(settings, newValue)
        }
    }
}