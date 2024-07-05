package com.kappdev.wordbook.main_feature.presentation.cards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.util.TextToSpeechHelper
import com.kappdev.wordbook.main_feature.domain.model.CollectionPreview
import com.kappdev.wordbook.main_feature.domain.repository.CardsOrderSaver
import com.kappdev.wordbook.main_feature.domain.use_case.DeleteCardById
import com.kappdev.wordbook.main_feature.domain.use_case.GetCollectionCards
import com.kappdev.wordbook.main_feature.domain.use_case.GetCollectionLanguage
import com.kappdev.wordbook.main_feature.domain.use_case.GetCollectionName
import com.kappdev.wordbook.main_feature.domain.use_case.GetCollectionsPreview
import com.kappdev.wordbook.main_feature.domain.use_case.MoveCardTo
import com.kappdev.wordbook.main_feature.domain.util.CardsOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CardsViewModel @Inject constructor(
    private val getCollectionsPreview: GetCollectionsPreview,
    private val getCollectionCards: GetCollectionCards,
    private val getCollectionName: GetCollectionName,
    private val getCollectionLanguage: GetCollectionLanguage,
    private val deleteCardById: DeleteCardById,
    private val moveCardTo: MoveCardTo,
    private val textToSpeechHelper: TextToSpeechHelper,
    private val orderSaver: CardsOrderSaver
) : ViewModel() {

    private var collectionId: Int? = null
    private var collectionLanguage: Locale? = null

    var cardsState by mutableStateOf(CardsState.Idle)
        private set

    var searchArg by mutableStateOf("")
        private set

    var order by mutableStateOf(orderSaver.getOrder())
        private set

    var collections by mutableStateOf<List<CollectionPreview>>(emptyList())
        private set

    var collectionName by mutableStateOf<String?>(null)
        private set

    var cards by mutableStateOf<List<Card>>(emptyList())
        private set

    private var cardsJob: Job? = null
    private var searchJob: Job? = null
    private var collectionsJob: Job? = null

    init {
        getCollections()
    }

    fun pendingSearch(arg: String) {
        searchArg = arg
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(400)
            refreshCards()
        }
    }

    fun updateOrder(newOrder: CardsOrder) {
        order = newOrder
        orderSaver.saveOrder(newOrder)
        refreshCards()
    }

    fun speak(text: String) {
        collectionLanguage?.let { language ->
            textToSpeechHelper.say(text, language)
        }
    }

    fun refreshCards() = collectionId?.let(::getCards)

    private fun getCards(id: Int) {
        cardsJob?.cancel()
        cardsJob = viewModelScope.launch(Dispatchers.IO) {
            startLoading()
            getCollectionCards(id, searchArg, order).collectLatest { data ->
                cards = data
                finishLoading()
            }
        }
    }

    private fun startLoading() {
        cardsState = when {
            searchArg.isNotEmpty() -> CardsState.Searching
            else -> CardsState.Loading
        }
    }

    private fun finishLoading() {
        cardsState = when {
            cards.isEmpty() && searchArg.isNotEmpty() -> CardsState.EmptySearch
            cards.isEmpty() -> CardsState.Empty
            else -> CardsState.Ready
        }
    }

    fun getCollectionInfo(collectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionName = getCollectionName(collectionId)
            collectionLanguage = getCollectionLanguage(collectionId)
        }
    }

    fun deleteCard(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteCardById(id)
        }
    }

    fun moveTo(cardId: Int, newCollectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            moveCardTo(cardId, newCollectionId)
        }
    }

    private fun getCollections() {
        collectionsJob?.cancel()
        collectionsJob = getCollectionsPreview().onEach { collections = it }.launchIn(viewModelScope)
    }

    fun setCollectionId(id: Int) {
        this.collectionId = id
    }

}