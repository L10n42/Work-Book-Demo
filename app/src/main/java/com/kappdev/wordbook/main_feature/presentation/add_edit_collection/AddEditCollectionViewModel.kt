package com.kappdev.wordbook.main_feature.presentation.add_edit_collection

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canhub.cropper.CropImageView
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.model.Collection
import com.kappdev.wordbook.core.domain.util.DialogState
import com.kappdev.wordbook.core.domain.util.Result
import com.kappdev.wordbook.core.domain.util.SnackbarState
import com.kappdev.wordbook.core.domain.util.mutableDialogStateOf
import com.kappdev.wordbook.main_feature.domain.use_case.DownloadImage
import com.kappdev.wordbook.main_feature.domain.use_case.GetCollectionById
import com.kappdev.wordbook.main_feature.domain.use_case.InsertCollection
import com.kappdev.wordbook.main_feature.domain.util.Image
import com.kappdev.wordbook.main_feature.domain.util.delete
import com.kappdev.wordbook.main_feature.domain.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class AddEditCollectionViewModel @Inject constructor(
    private val insertCollection: InsertCollection,
    private val getCollectionById: GetCollectionById,
    private val downloadImage: DownloadImage
) : ViewModel() {

    private var collectionId: Int? = null
    private var originalCollection: Collection? = null

    val snackbarState = SnackbarState(viewModelScope)

    private var _loadingDialog = mutableDialogStateOf<Int?>(null)
    val loadingDialog: DialogState<Int?> = _loadingDialog

    var name by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var termLanguage by mutableStateOf(Locale.UK)
        private set

    var definitionLanguage by mutableStateOf(Locale.UK)
        private set

    var cover by mutableStateOf<Image>(Image.Empty)
        private set

    var color by mutableStateOf<Color?>(null)
        private set

    fun getCollection(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getCollectionById(id)?.unpack()
        }
    }

    fun saveCollection(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingDialog.showDialog(R.string.saving)
            val result = insertCollection(packCollection(), cover)
            _loadingDialog.hideDialog()
            when (result) {
                is Result.Failure -> snackbarState.show(result.messageResId)
                is Result.Success -> withContext(Dispatchers.Main) { onSuccess() }
            }
        }
    }

    fun handleCropImageResult(result: CropImageView.CropResult) {
        if (result.isSuccessful) {
            result.uriContent?.let(::updateCover)
        }
    }

    fun downloadImageFromUrl(url: String, onSuccess: (Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingDialog.showDialog(R.string.downloading)
            val result = downloadImage(url)
            _loadingDialog.hideDialog()
            when (result) {
                is Result.Success -> withContext(Dispatchers.Main) { onSuccess(result.value) }
                is Result.Failure -> snackbarState.show(result.messageResId)
            }
        }
    }

    private fun packCollection(): Collection {
        return Collection(
            id = collectionId ?: 0,
            name = name.trim(),
            description = description.trim(),
            termLanguage = termLanguage,
            definitionLanguage = definitionLanguage,
            backgroundImage = null,
            color = color,
            created = originalCollection?.created ?: System.currentTimeMillis(),
            lastEdit = System.currentTimeMillis()
        )
    }

    private fun Collection.unpack() {
        collectionId = this.id
        updateName(this.name)
        updateDescription(this.description)
        updateColor(this.color)
        updateTermLanguage(this.termLanguage)
        updateDefinitionLanguage(this.definitionLanguage)
        cover = this.backgroundImage?.let(Image::Stored) ?: Image.Empty
        originalCollection = this
    }

    fun hasUnsavedChanges(): Boolean {
        return if (originalCollection == null) {
            name.isNotEmpty() || description.isNotEmpty() || cover != Image.Empty
        } else {
            name != originalCollection?.name || description != originalCollection?.description ||
                    color != originalCollection?.color || termLanguage != originalCollection?.termLanguage ||
                         definitionLanguage != originalCollection?.definitionLanguage ||
                             (cover != Image.Empty && cover !is Image.Stored)
        }
    }

    private fun updateCover(newCover: Uri) {
        this.cover = cover.update(newCover)
    }

    fun updateColor(color: Color?) {
        this.color = color
    }

    fun deleteCover() {
        this.cover = cover.delete()
    }

    fun updateName(value: String) {
        this.name = value
    }

    fun updateDescription(value: String) {
        this.description = value
    }

    fun updateTermLanguage(value: Locale) {
        this.termLanguage = value
    }

    fun updateDefinitionLanguage(value: Locale) {
        this.definitionLanguage = value
    }
}