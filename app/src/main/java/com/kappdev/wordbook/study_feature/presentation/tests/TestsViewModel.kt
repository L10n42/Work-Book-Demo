package com.kappdev.wordbook.study_feature.presentation.tests

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TestsViewModel @Inject constructor(
    private val getCards: GetCards,
    private val ttsHelper: TextToSpeechHelper,
    private val getCollectionLanguages: GetCollectionLanguages
) : ViewModel() {

    private var collectionLanguages: CollectionLanguages? = null
    private var currentCollectionId: Int? = null
    private var allCards: List<Card> = emptyList()

    var studyState by mutableStateOf(StudyState.Idle)
        private set

    var totalCardsSize by mutableStateOf(0)
        private set

    var testState by mutableStateOf<TestState?>(null)
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
        allCards = data
        updateTest()
    }

    fun answer(index: Int) {
        testState = testState?.answer(index)
    }

    fun nextTest() {
        if (_unlearnedCards.isNotEmpty()) {
            _learnedCards.add(_unlearnedCards.removeFirst())
            updateTest()
            updateState()
        }
    }

    private fun updateTest() {
        if (_unlearnedCards.isNotEmpty() && allCards.size >= 4) {
            val questionCard = unlearnedCards.first()
            val answers = mutableListOf(questionCard)

            while (answers.size < 4) {
                val randomCard = allCards.random()
                if (randomCard !in answers) answers.add(randomCard)
            }
            testState = TestState.from(questionCard, answers.shuffled())
        }
    }

    fun speakDefinition(text: String) {
        collectionLanguages?.definition?.let { ttsHelper.say(text, it) }
    }

    fun resetTests() {
        _unlearnedCards.clear()
        _unlearnedCards.addAll(learnedCards.shuffled())
        _learnedCards.clear()
        updateTest()
        updateState()
    }

    private fun updateState() {
        studyState = when {
            unlearnedCards.isEmpty() && learnedCards.isNotEmpty() -> StudyState.Finished
            unlearnedCards.isNotEmpty() -> StudyState.Studying
            else -> StudyState.Idle
        }
    }
}