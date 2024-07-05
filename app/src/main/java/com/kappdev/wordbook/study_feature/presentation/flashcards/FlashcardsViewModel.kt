package com.kappdev.wordbook.study_feature.presentation.flashcards

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.util.TextToSpeechHelper
import com.kappdev.wordbook.study_feature.domain.model.CollectionLanguages
import com.kappdev.wordbook.study_feature.domain.use_case.GetCards
import com.kappdev.wordbook.study_feature.domain.use_case.GetCollectionLanguages
import com.kappdev.wordbook.study_feature.presentation.common.StudyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class FlashcardsViewModel @Inject constructor(
    private val getCards: GetCards,
    private val ttsHelper: TextToSpeechHelper,
    private val getCollectionLanguages: GetCollectionLanguages
) : ViewModel() {

    private var collectionLanguages: CollectionLanguages? = null
    private var currentCollectionId: Int? = null

    private var _swipeCardFlow = MutableSharedFlow<SwipeInfo>()
    val swipeCardFlow: SharedFlow<SwipeInfo> = _swipeCardFlow

    var studyState by mutableStateOf(StudyState.Idle)
        private set

    var totalCardsSize by mutableStateOf(0)
        private set

    private val _learnedCards = mutableStateListOf<Card>()
    val learnedCards: List<Card> = _learnedCards

    private val _unlearnedCards = mutableStateListOf<Card>()
    val unlearnedCards: List<Card> = _unlearnedCards

    fun getDataIfNeed(collectionId: Int) {
        if (currentCollectionId == null) {
            currentCollectionId = collectionId
            getLanguages(collectionId)
            getData(collectionId)
        }
    }

    private fun getLanguages(collectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            collectionLanguages = getCollectionLanguages(collectionId)
        }
    }

    private fun getData(collectionId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            studyState = StudyState.Loading
            updateData(getCards(collectionId))
            studyState = StudyState.Studying
        }
    }

    private fun updateData(data: List<Card>) {
        _unlearnedCards.clear()
        _unlearnedCards.addAll(data.shuffled())
        totalCardsSize = data.size
    }

    fun popLastCard() {
        if (learnedCards.isNotEmpty()) {
            _unlearnedCards.add(0, _learnedCards.removeLast())
            updateState()
        }
    }

    fun learn() {
        if (_unlearnedCards.isNotEmpty()) {
            _learnedCards.add(_unlearnedCards.removeFirst())
            updateState()
        }
    }

    fun relearn() {
        if (_unlearnedCards.isEmpty()) return

        if (unlearnedCards.size > 3) {
            val newIndex = Random.nextInt(2, unlearnedCards.lastIndex)
            _unlearnedCards.add(newIndex, _unlearnedCards.removeFirst())
        } else {
            _unlearnedCards.add(_unlearnedCards.removeFirst())
        }
    }

    fun speakTerm(text: String) {
        collectionLanguages?.term?.let { ttsHelper.say(text, it) }
    }

    fun speakDefinition(text: String) {
        collectionLanguages?.definition?.let { ttsHelper.say(text, it) }
    }

    fun resetFlashcards() {
        _unlearnedCards.clear()
        _unlearnedCards.addAll(learnedCards.shuffled())
        _learnedCards.clear()
        updateState()
    }

    fun swipeCardWithAnimation(direction: CardDragDirection) {
        if (unlearnedCards.isEmpty()) return
        viewModelScope.launch {
            _swipeCardFlow.emit(SwipeInfo(unlearnedCards.first(), direction))
        }
    }
 
    private fun updateState() {
        studyState = when {
            unlearnedCards.isEmpty() && learnedCards.isNotEmpty() -> StudyState.Finished
            unlearnedCards.isNotEmpty() -> StudyState.Studying
            else -> StudyState.Idle
        }
    }
}