package com.example.presentation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

class VoiceAssistantManager(
    private val context: Context,
    private val onStateChange: (VoiceState) -> Unit,
    private val onTextRecognized: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var speechRecognizer: SpeechRecognizer? = null

    init {
        tts = TextToSpeech(context, this)
        setupSpeechRecognizer()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}
                override fun onDone(utteranceId: String?) {}
                @Deprecated("Deprecated in Java", ReplaceWith("Unit"))
                override fun onError(utteranceId: String?) {}
            })
        }
    }

    fun getAvailableVoices(): List<android.speech.tts.Voice> {
        return try {
            tts?.voices?.toList() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun setVoice(voiceName: String) {
        val voice = getAvailableVoices().find { it.name == voiceName }
        if (voice != null) {
            tts?.voice = voice
        }
    }

    private fun setupSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
            speechRecognizer?.setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    onStateChange(VoiceState.Listening)
                }
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    onStateChange(VoiceState.Processing)
                }
                override fun onError(error: Int) {
                    onStateChange(VoiceState.Error("Voice recognition error: $error"))
                }
                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        onTextRecognized(text)
                    } else {
                        onStateChange(VoiceState.Idle)
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        }
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        onStateChange(VoiceState.Idle)
    }

    fun speak(text: String) {
        val maxLen = TextToSpeech.getMaxSpeechInputLength()
        if (text.length > maxLen) {
            val chunks = text.chunked(maxLen)
            chunks.forEachIndexed { index, chunk ->
                tts?.speak(chunk, if (index == 0) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD, null, "chunk_$index")
            }
        } else {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "full_text")
        }
    }

    fun stopSpeaking() {
        tts?.stop()
    }

    fun exportAudio(text: String, outputFile: java.io.File) {
        tts?.synthesizeToFile(text, null, outputFile, "export_audio")
    }

    fun destroy() {
        tts?.stop()
        tts?.shutdown()
        speechRecognizer?.destroy()
    }
}
