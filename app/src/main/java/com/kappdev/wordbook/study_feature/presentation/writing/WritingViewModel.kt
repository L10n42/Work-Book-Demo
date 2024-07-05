package com.kappdev.wordbook.study_feature.presentation.writing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.study_feature.domain.use_case.GetCards
import com.kappdev.wordbook.study_feature.presentation.common.StudyState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WritingViewModel @Inject constructor(
    private val getCards: GetCards,
) : ViewModel() {

    private var currentCollectionId: Int? = null

    var studyState by mutableStateOf(StudyState.Idle)
        private set

    var totalCardsSize by mutableStateOf(0)
        private set

    var writingState by mutableStateOf<WritingState?>(null)
        private set

    private val _learnedCards = mutableStateListOf<Card>()
    val learnedCards: List<Card> = _learnedCards

    private val _unlearnedCards = mutableStateListOf<Card>()
    val unlearnedCards: List<Card> = _unlearnedCards

    fun getDataIfNeed(collectionId: Int) {
        if (currentCollectionId == null) {
            currentCollectionId = collectionId
            getData(collectionId)
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
        updateWritingState()
    }

    fun submitAnswer() {
        writingState = writingState?.answer()
    }

    fun showAnswer() {
        writingState = writingState?.showAnswer()
    }

    fun nextCard() {
        if (_unlearnedCards.isNotEmpty()) {
            _learnedCards.add(_unlearnedCards.removeFirst())
            updateWritingState()
            updateState()
        }
    }

    private fun updateWritingState() {
        if (_unlearnedCards.isNotEmpty()) {
            writingState = WritingState.from(unlearnedCards.first())
        }
    }

    fun resetWriting() {
        _unlearnedCards.clear()
        _unlearnedCards.addAll(learnedCards.shuffled())
        _learnedCards.clear()
        updateWritingState()
        updateState()
    }

    private fun updateState() {
        studyState = when {
            unlearnedCards.isEmpty() && learnedCards.isNotEmpty() -> StudyState.Finished
            unlearnedCards.isNotEmpty() -> StudyState.Studying
            else -> StudyState.Idle
        }
    }

    fun updateAnswer(newAnswer: String) {
        writingState = writingState?.copy(answer = newAnswer)
    }
}