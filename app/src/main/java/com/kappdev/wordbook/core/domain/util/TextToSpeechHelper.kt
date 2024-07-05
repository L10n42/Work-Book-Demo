package com.kappdev.wordbook.core.domain.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

class TextToSpeechHelper(context: Context): TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech
    private var status: Int = TextToSpeech.ERROR

    var availableLanguages = mutableStateOf(emptyList<Locale>())
        private set

    var isSpeaking = mutableStateOf(false)
        private set

    init {
        textToSpeech = TextToSpeech(context, this)
        registerUtteranceProgressListener()
    }

    fun say(text: String, language: Locale) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech.let { tts ->
                if (tts.isSpeaking) tts.stop()
                tts.language = language
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    private fun registerUtteranceProgressListener() {
        textToSpeech.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    isSpeaking.value = false
                }

                override fun onError(utteranceId: String?) {
                    isSpeaking.value = false
                }
            }
        )
    }

    override fun onInit(status: Int) {
        this.status = status
        if (status == TextToSpeech.SUCCESS) {
            availableLanguages.value = textToSpeech.availableLanguages.sortedBy { it.displayName }
        }
    }
}