package com.bharosa.guardian.service

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false
    private val pendingSpeechQueue = mutableListOf<Pair<String, String>>()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            // Process queue if any speech request came before init
            for ((text, lang) in pendingSpeechQueue) {
                speakInternal(text, lang)
            }
            pendingSpeechQueue.clear()
        } else {
            Log.e(TAG, "TTS Initialization Failed with status $status")
        }
    }

    fun speak(text: String, langCode: String = "hi") {
        if (!isInitialized) {
            pendingSpeechQueue.add(Pair(text, langCode))
            return
        }
        speakInternal(text, langCode)
    }

    private fun speakInternal(text: String, langCode: String) {
        val targetLocale = when (langCode.lowercase()) {
            "hi", "hin" -> Locale("hi", "IN")
            else -> Locale.ENGLISH
        }

        val result = tts?.setLanguage(targetLocale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.w(TAG, "Requested language $langCode not supported by device TTS engine. Falling back to English.")
            tts?.language = Locale.ENGLISH
        }

        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "BHAROSA_TTS_${System.currentTimeMillis()}")
    }

    fun stop() {
        if (isInitialized) {
            tts?.stop()
        }
    }

    fun shutdown() {
        if (isInitialized) {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        }
    }

    companion object {
        private const val TAG = "BharosaTtsManager"
    }
}
