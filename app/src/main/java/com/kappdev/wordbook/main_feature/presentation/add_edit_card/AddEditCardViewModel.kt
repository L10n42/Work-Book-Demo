package com.kappdev.wordbook.main_feature.presentation.add_edit_card

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.canhub.cropper.CropImageView
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.util.DialogState
import com.kappdev.wordbook.core.domain.util.Result
import com.kappdev.wordbook.core.domain.util.SnackbarState
import com.kappdev.wordbook.core.domain.util.mutableDialogStateOf
import com.kappdev.wordbook.main_feature.domain.model.TermDuplicate
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview
import com.kappdev.wordbook.main_feature.domain.use_case.DownloadImage
import com.kappdev.wordbook.main_feature.domain.use_case.FindDuplicates
import com.kappdev.wordbook.main_feature.domain.use_case.GetCardById
import com.kappdev.wordbook.main_feature.domain.use_case.GetCollectionPreview
import com.kappdev.wordbook.main_feature.domain.use_case.GetCollectionsPreview
import com.kappdev.wordbook.main_feature.domain.use_case.InsertCard
import com.kappdev.wordbook.main_feature.domain.util.Image
import com.kappdev.wordbook.main_feature.domain.util.delete
import com.kappdev.wordbook.main_feature.domain.util.update
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddEditCardViewModel @Inject constructor(
    private val insertCard: InsertCard,
    private val getCardById: GetCardById,
    private val downloadImage: DownloadImage,
    private val getCollectionsPreview: GetCollectionsPreview,
    private val getCollectionPreview: GetCollectionPreview,
    private val findDuplicates: FindDuplicates
) : ViewModel() {

    private var cardId: Int? = null
    private var originalCard: Card? = null

    val snackbarState = SnackbarState(viewModelScope)

    private var _loadingDialog = mutableDialogStateOf<Int?>(null)
    val loadingDialog: DialogState<Int?> = _loadingDialog

    var collections by mutableStateOf<List<CollectionPreview>>(emptyList())
        private set

    var selectedCollection by mutableStateOf<CollectionPreview?>(null)
        private set

    var term by mutableStateOf("")
        private set

    var transcription by mutableStateOf("")
        private set

    var definition by mutableStateOf("")
        private set

    var example by mutableStateOf("")
        private set

    var cardImage by mutableStateOf<Image>(Image.Empty)
        private set

    private var collectionsJob: Job? = null

    fun getCard(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getCardById(id)?.unpack()
        }
    }

    fun getCollection(collectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            selectedCollection = getCollectionPreview(collectionId)
        }
    }

    fun getCollections() {
        collectionsJob?.cancel()
        collectionsJob =
            getCollectionsPreview().onEach(::updateCollections).launchIn(viewModelScope)
    }

    fun saveAndAnother(onSuccess: () -> Unit = {}) = saveCard { onSuccess(); resetCard() }

    fun findDuplicates(onResult: (List<TermDuplicate>) -> Unit) {
        if (cardId != null) {
            onResult(emptyList())
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _loadingDialog.showDialog(R.string.checking_for_duplicates)
            val duplicates = findDuplicates(term.trim())
            _loadingDialog.hideDialog()
            onResult(duplicates)
        }
    }

    fun saveCard(onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            packCard()?.let { packedCard ->
                _loadingDialog.showDialog(R.string.saving)
                val result = insertCard(packedCard, cardImage)
                _loadingDialog.hideDialog()
                when (result) {
                    is Result.Failure -> snackbarState.show(result.messageResId)
                    is Result.Success -> withContext(Dispatchers.Main) { onSuccess() }
                }
            }
        }
    }

    fun handleCropImageResult(result: CropImageView.CropResult) {
        if (result.isSuccessful) {
            result.uriContent?.let(::updateImage)
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

    private fun packCard(): Card? {
        val collectionId = selectedCollection?.id
        return if (collectionId != null) {
            Card(
                id = cardId ?: 0,
                collectionId = collectionId,
                term = term.trim(),
                transcription = transcription.trim(),
                definition = definition.trim(),
                example = example.trim(),
                image = null,
                created = originalCard?.created ?: System.currentTimeMillis(),
                lastEdit = System.currentTimeMillis()
            )
        } else {
            snackbarState.show(R.string.collection_not_selected_error)
            null
        }
    }

    private fun Card.unpack() {
        cardId = this.id
        getCollection(this.collectionId)
        updateTerm(this.term)
        updateTranscription(this.transcription)
        updateDefinition(this.definition)
        updateExample(this.example)
        cardImage = this.image?.let(Image::Stored) ?: Image.Empty
        originalCard = this
    }

    private fun resetCard() {
        cardId = null
        this.term = ""
        this.transcription = ""
        this.definition = ""
        this.example = ""
        cardImage = Image.Empty
        originalCard = null
    }

    fun hasUnsavedChanges(): Boolean {
        return if (originalCard == null) {
            term.isNotEmpty() || transcription.isNotEmpty() ||
                    definition.isNotEmpty() || example.isNotEmpty() || cardImage != Image.Empty
        } else {
            term != originalCard?.term || transcription != originalCard?.transcription ||
                    definition != originalCard?.definition || example != originalCard?.example ||
                    (cardImage != Image.Empty && cardImage !is Image.Stored) ||
                    selectedCollection?.id != originalCard?.collectionId
        }
    }

    private fun updateCollections(data: List<CollectionPreview>) {
        this.collections = data
    }

    private fun updateImage(newImage: Uri) {
        this.cardImage = cardImage.update(newImage)
    }

    fun selectCollection(collection: CollectionPreview) {
        this.selectedCollection = collection
    }

    fun deleteImage() {
        this.cardImage = cardImage.delete()
    }

    fun updateTerm(value: String) {
        this.term = value
    }

    fun updateTranscription(value: String) {
        this.transcription = value
    }

    fun updateDefinition(value: String) {
        this.definition = value
    }

    fun updateExample(value: String) {
        this.example = value
    }
}