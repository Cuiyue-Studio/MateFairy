package com.example.matefairy01.input

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * 语音输入提供者（预留实现）
 * 基于 Android SpeechRecognizer API，为后续真机语音输入功能提供基础框架
 */
class VoiceInputProvider(private val context: Context) : IUserInputProvider {

    private var speechRecognizer: SpeechRecognizer? = null
    private var isActive by mutableStateOf(false)
    private var resultCallback: ((String) -> Unit)? = null

    override val inputMode: InputMode = InputMode.VOICE

    override fun startListening(onResult: (String) -> Unit) {
        if (isActive) {
            stopListening()
        }

        isActive = true
        resultCallback = onResult

        // 初始化 SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {
                    Log.d("VoiceInputProvider", "Ready for speech")
                }

                override fun onBeginningOfSpeech() {
                    Log.d("VoiceInputProvider", "Beginning of speech")
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(buffer: ByteArray?) {}

                override fun onEndOfSpeech() {
                    Log.d("VoiceInputProvider", "End of speech")
                    isActive = false
                }

                override fun onError(error: Int) {
                    Log.e("VoiceInputProvider", "Speech recognition error: $error")
                    isActive = false
                }

                override fun onResults(results: Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        val text = matches[0]
                        Log.d("VoiceInputProvider", "Recognized: $text")
                        resultCallback?.invoke(text)
                    }
                    isActive = false
                }

                override fun onPartialResults(partialResults: Bundle?) {
                    val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    if (!matches.isNullOrEmpty()) {
                        Log.d("VoiceInputProvider", "Partial result: ${matches[0]}")
                    }
                }

                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }

        // 配置识别意图
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "zh-CN")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
        }

        speechRecognizer?.startListening(intent)
        Log.d("VoiceInputProvider", "Started listening")
    }

    override fun stopListening() {
        speechRecognizer?.stopListening()
        speechRecognizer?.destroy()
        speechRecognizer = null
        isActive = false
        Log.d("VoiceInputProvider", "Stopped listening")
    }

    override fun isListening(): Boolean = isActive
}
