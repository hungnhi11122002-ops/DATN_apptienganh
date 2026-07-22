package com.example.EnglishWithStork.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale

class EnglishTtsManager(context: Context) {

    private var textToSpeech: TextToSpeech? = null
    private var isReady: Boolean = false

    init {
        textToSpeech = TextToSpeech(
            context.applicationContext
        ) { status ->

            if (status == TextToSpeech.SUCCESS) {
                val engine = textToSpeech

                if (engine != null) {
                    val languageResult = engine.setLanguage(Locale.US)

                    isReady =
                        languageResult != TextToSpeech.LANG_MISSING_DATA &&
                                languageResult != TextToSpeech.LANG_NOT_SUPPORTED

                    if (isReady) {
                        // 1.0f là tốc độ bình thường.
                        // 0.85f chậm hơn một chút, phù hợp học từ vựng.
                        engine.setSpeechRate(0.85f)

                        // 1.0f là cao độ bình thường.
                        engine.setPitch(1.0f)
                    }
                }
            } else {
                isReady = false
            }
        }
    }

    /**
     * @return true nếu yêu cầu đọc được gửi thành công.
     */
    fun speak(text: String): Boolean {
        if (!isReady || text.isBlank()) {
            return false
        }

        val result = textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "vocabulary_${System.nanoTime()}"
        )

        return result == TextToSpeech.SUCCESS
    }

    /**
     * Dừng đọc và giải phóng tài nguyên.
     */
    fun release() {
        isReady = false

        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}