package com.kappdev.wordbook.main_feature.presentation.collections

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappdev.wordbook.R
import com.kappdev.wordbook.core.domain.util.DialogState
import com.kappdev.wordbook.core.domain.util.SnackbarState
import com.kappdev.wordbook.core.domain.util.mutableDialogStateOf
import com.kappdev.wordbook.main_feature.domain.model.CollectionInfo
import com.kappdev.wordbook.main_feature.domain.repository.CollectionsOrderSaver
import com.kappdev.wordbook.main_feature.domain.use_case.DeleteCollectionById
import com.kappdev.wordbook.main_feature.domain.use_case.GetCollectionsInfo
import com.kappdev.wordbook.main_feature.domain.util.CollectionsOrder
import com.kappdev.wordbook.share_feature.domain.repository.ShareCollection
import com.kappdev.wordbook.share_feature.domain.repository.ShareCollectionAsPDF
import com.kappdev.wordbook.share_feature.domain.util.CollectionPDFResult
import com.kappdev.wordbook.share_feature.domain.util.ShareCollectionResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val getCollectionsInfo: GetCollectionsInfo,
    private val deleteCollectionById: DeleteCollectionById,
    private val orderSaver: CollectionsOrderSaver,
    private val shareCollectionsAsPDF: ShareCollectionAsPDF,
    private val shareCollection: ShareCollection
) : ViewModel() {

    val snackbarState = SnackbarState(viewModelScope)

    private var _loadingDialog = mutableDialogStateOf<Int?>(null)
    val loadingDialog: DialogState<Int?> = _loadingDialog

    var collectionsState by mutableStateOf(CollectionsState.Idle)
        private set

    var searchArg by mutableStateOf("")
        private set

    var order by mutableStateOf(orderSaver.getOrder())
        private set

    var collections by mutableStateOf<List<CollectionInfo>>(emptyList())
        private set

    private var collectionsJob: Job? = null
    private var searchJob: Job? = null

    fun updateOrder(newOrder: CollectionsOrder) {
        order = newOrder
        orderSaver.saveOrder(newOrder)
        getCollections()
    }

    fun pendingSearch(arg: String) {
        searchArg = arg
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(400)
            getCollections()
        }
    }

    fun getCollections() {
        collectionsJob?.cancel()
        collectionsJob = viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            getCollectionsInfo(searchArg, order).collectLatest { data ->
                collections = data
                finishLoading()
            }
        }
    }

    private fun startLoading() {
        collectionsState = when {
            searchArg.isNotEmpty() -> CollectionsState.Searching
            else -> CollectionsState.Loading
        }
    }

    private fun finishLoading() {
        collectionsState = when {
            collections.isEmpty() && searchArg.isNotEmpty() -> CollectionsState.EmptySearch
            collections.isEmpty() -> CollectionsState.Empty
            else -> CollectionsState.Ready
        }
    }

    fun shareCollection(collectionId: Int, onSuccess: (Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingDialog.showDialog(R.string.generating)
            val shareResult = shareCollection.createCollectionZip(collectionId)
            _loadingDialog.hideDialog()

            when (shareResult) {
                ShareCollectionResult.CollectionError -> snackbarState.show(R.string.find_collection_error)
                ShareCollectionResult.EmptyCollection -> snackbarState.show(R.string.at_least_1_card_error)
                is ShareCollectionResult.Success -> withContext(Dispatchers.Main) { onSuccess(shareResult.uri) }
            }
        }
    }

    fun shareCollectionPdf(collectionId: Int, layoutDirection: LayoutDirection, onSuccess: (Uri) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadingDialog.showDialog(R.string.generating_pdf)
            val pdfResult = shareCollectionsAsPDF.createCollectionPDF(collectionId, layoutDirection)
            _loadingDialog.hideDialog()

            when (pdfResult) {
                CollectionPDFResult.EmptyCollection -> snackbarState.show(R.string.at_least_1_card_error)
                is CollectionPDFResult.Success ->  withContext(Dispatchers.Main) { onSuccess(pdfResult.uri) }
            }
        }
    }

    fun showSnackbar(@StringRes stringRes: Int) {
        snackbarState.show(stringRes)
    }

    fun deleteCollection(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCollectionById(id)
        }
    }
}