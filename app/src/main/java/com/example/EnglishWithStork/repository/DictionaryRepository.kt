package com.example.EnglishWithStork.repository
import com.example.EnglishWithStork.Models.DictionaryLookupResult
import com.example.EnglishWithStork.Models.DictionaryLookupState
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyDao
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity
import com.example.EnglishWithStork.network.DictionaryNetwork
import kotlinx.coroutines.CancellationException
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume



class DictionaryRepository(

    private val vocabularyDao: VocabularyDao

) {

    /**
     * Luồng tìm từ:
     *
     * 1. Luôn ưu tiên Room.
     * 2. Nếu Room không có:
     *      allowOnline = false -> dừng.
     *      allowOnline = true  -> gọi API.
     */
    suspend fun lookup(

        keyword: String,

        allowOnline: Boolean

    ): DictionaryLookupState {

        val word =
            keyword
                .trim()

        if (word.isBlank()) {

            return DictionaryLookupState.NotFound
        }


        // =============================================
        // BƯỚC 1 - ROOM DATABASE
        // =============================================

        val localVocabulary =
            vocabularyDao
                .findExactEnglishWord(word)


        if (localVocabulary != null) {

            return DictionaryLookupState.Found(

                localVocabulary
                    .toDictionaryResult()
            )
        }


        // =============================================
        // Không gọi Internet khi user chỉ đang gõ
        // =============================================

        if (!allowOnline) {

            return DictionaryLookupState.OfflineMiss
        }


        // =============================================
        // BƯỚC 2 - ONLINE
        // =============================================

        return lookupOnline(word)
    }

    private val translator by lazy {

        val options =
            TranslatorOptions
                .Builder()
                .setSourceLanguage(
                    TranslateLanguage.ENGLISH
                )
                .setTargetLanguage(
                    TranslateLanguage.VIETNAMESE
                )
                .build()

        Translation.getClient(options)
    }

    private suspend fun translateToVietnamese(
        english: String
    ): String? {

        val modelReady =
            suspendCancellableCoroutine<Boolean> { continuation ->

                translator
                    .downloadModelIfNeeded()
                    .addOnSuccessListener {

                        if (continuation.isActive) {
                            continuation.resume(true)
                        }
                    }
                    .addOnFailureListener {

                        if (continuation.isActive) {
                            continuation.resume(false)
                        }
                    }
            }


        if (!modelReady) {
            return null
        }


        return suspendCancellableCoroutine { continuation ->

            translator
                .translate(english)
                .addOnSuccessListener { translated ->

                    if (continuation.isActive) {

                        continuation.resume(
                            translated
                                .trim()
                                .takeIf {
                                    it.isNotBlank()
                                }
                        )
                    }
                }
                .addOnFailureListener {

                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
        }
    }
    private suspend fun lookupOnline(
        keyword: String
    ): DictionaryLookupState {

        // =================================================
        // API CHÍNH: DATAMUSE
        // =================================================

        try {

            android.util.Log.d(
                "DICTIONARY_TEST",
                "START Datamuse: $keyword"
            )


            val result =
                lookupFromDatamuse(
                    keyword
                )


            if (result != null) {

                android.util.Log.d(
                    "DICTIONARY_TEST",
                    "Datamuse SUCCESS"
                )


                return DictionaryLookupState.Found(
                    result
                )
            }


        } catch (e: CancellationException) {

            throw e


        } catch (e: Exception) {

            android.util.Log.e(
                "DICTIONARY_TEST",
                "Datamuse ERROR -> thử FreeDictionary",
                e
            )
        }


        // =================================================
        // API DỰ PHÒNG: FREE DICTIONARY
        // =================================================

        try {

            android.util.Log.d(
                "DICTIONARY_TEST",
                "START FreeDictionary FALLBACK: $keyword"
            )


            val result =
                lookupFromFreeDictionary(
                    keyword
                )


            if (result != null) {

                return DictionaryLookupState.Found(
                    result
                )
            }


            return DictionaryLookupState.NotFound


        } catch (e: CancellationException) {

            throw e


        } catch (e: java.net.UnknownHostException) {

            return DictionaryLookupState.Error(
                "Không thể kết nối Internet."
            )


        } catch (e: java.net.SocketTimeoutException) {

            return DictionaryLookupState.Error(
                "Máy chủ từ điển phản hồi quá chậm."
            )


        } catch (e: Exception) {

            android.util.Log.e(
                "DICTIONARY_TEST",
                "FreeDictionary FALLBACK ERROR",
                e
            )


            return DictionaryLookupState.Error(
                "Không thể tra từ online lúc này."
            )
        }
    }

    private suspend fun lookupFromFreeDictionary(
        keyword: String
    ): DictionaryLookupResult? {

        val response =
            DictionaryNetwork
                .dictionaryApi
                .lookupWord(keyword)


        android.util.Log.d(
            "DICTIONARY_TEST",
            "END FreeDictionary - HTTP ${response.code()}"
        )


        if (!response.isSuccessful) {

            return null
        }


        val entry =
            response
                .body()
                ?.firstOrNull()
                ?: return null


        val meanings =
            entry.meanings.orEmpty()


        val partOfSpeech =
            meanings
                .firstOrNull {
                    !it.partOfSpeech.isNullOrBlank()
                }
                ?.partOfSpeech


        val definitionDto =
            meanings
                .asSequence()
                .flatMap {
                    it.definitions
                        .orEmpty()
                        .asSequence()
                }
                .firstOrNull {
                    !it.definition.isNullOrBlank()
                }


        val phonetic =
            entry
                .phonetic
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: entry
                    .phonetics
                    .orEmpty()
                    .mapNotNull {

                        it.text
                            ?.takeIf { text ->
                                text.isNotBlank()
                            }
                    }
                    .firstOrNull()


        val englishWord =
            entry
                .word
                ?.takeIf {
                    it.isNotBlank()
                }
                ?: keyword


        val vietnamese =
            translateToVietnamese(
                englishWord
            )


        return DictionaryLookupResult(

            english =
                englishWord,

            phonetic =
                phonetic,

            wordClass =
                partOfSpeech,

            vietnamese =
                vietnamese,

            definition =
                definitionDto?.definition,

            exampleEnglish =
                findExample(meanings),

            exampleVietnamese =
                null,

            localVocabularyId =
                null
        )
    }

    private suspend fun lookupFromDatamuse(
        keyword: String
    ): DictionaryLookupResult? {

        val response =
            DictionaryNetwork
                .datamuseApi
                .lookupWord(
                    word = keyword
                )


        android.util.Log.d(
            "DICTIONARY_TEST",
            "END Datamuse - HTTP ${response.code()}"
        )


        if (!response.isSuccessful) {

            return null
        }


        val item =
            response
                .body()
                ?.firstOrNull()
                ?: return null


        val returnedWord =
            item
                .word
                ?.trim()
                ?: return null


        // Không cho Datamuse trả một từ gần giống
        // thay cho từ user thực sự nhập.
        if (
            !returnedWord.equals(
                keyword,
                ignoreCase = true
            )
        ) {

            return null
        }


        // ==========================================
        // DEFINITION
        // ==========================================

        val rawDefinition =
            item
                .defs
                ?.firstOrNull()


        // Ví dụ:
        // n    good fortune...
        //
        // Bỏ "n\t" ở đầu.
        val definition =
            rawDefinition
                ?.substringAfter(
                    "\t",
                    rawDefinition
                )
                ?.trim()


        // ==========================================
        // PART OF SPEECH
        // ==========================================

        val partOfSpeechTag =
            item
                .tags
                .orEmpty()
                .firstOrNull {

                    it == "n" ||
                            it == "v" ||
                            it == "adj" ||
                            it == "adv"
                }


        val partOfSpeech =
            when (partOfSpeechTag) {

                "n" ->
                    "noun"

                "v" ->
                    "verb"

                "adj" ->
                    "adjective"

                "adv" ->
                    "adverb"

                else ->
                    null
            }


        // ==========================================
        // PRONUNCIATION
        // ==========================================

        // ==========================================
// PHONETIC - IPA
// ==========================================

        val rawPhonetic =
            item
                .tags
                .orEmpty()
                .firstOrNull {
                    it.startsWith("ipa_pron:")
                }
                ?.removePrefix("ipa_pron:")
                ?.trim()


        android.util.Log.d(
            "DICTIONARY_IPA",
            "tags=${item.tags}, IPA=$rawPhonetic"
        )


        val phonetic =
            rawPhonetic
                ?.takeIf {
                    it.isNotBlank()
                }
                ?.let {
                    "/$it/"
                }


        // ==========================================
        // VIETNAMESE
        // ==========================================

        val vietnamese =
            translateToVietnamese(
                returnedWord
            )


        return DictionaryLookupResult(

            english =
                returnedWord,

            phonetic =
                phonetic,

            wordClass =
                partOfSpeech,

            vietnamese =
                vietnamese,

            definition =
                definition,

            exampleEnglish =
                null,

            exampleVietnamese =
                null,

            localVocabularyId =
                null
        )
    }


    /**
     * Tìm example đầu tiên có dữ liệu.
     */
    private fun findExample(
        meanings: List<com.example.EnglishWithStork.network.MeaningDto>
    ): String? {

        return meanings
            .asSequence()
            .flatMap {

                it.definitions
                    .orEmpty()
                    .asSequence()
            }
            .mapNotNull {

                it.example
                    ?.takeIf { example ->
                        example.isNotBlank()
                    }
            }
            .firstOrNull()
    }



    /**
     * Chuyển dữ liệu Room sang model dùng chung.
     */
    private fun VocabularyEntity.toDictionaryResult():
            DictionaryLookupResult {

        return DictionaryLookupResult(

            english =
                english,

            phonetic =
                phonetic,

            wordClass =
                wordClass,

            vietnamese =
                vietnamese,

            definition =
                null,

            exampleEnglish =
                exampleEnglish,

            exampleVietnamese =
                exampleVietnamese,

            localVocabularyId =
                id
        )
    }
}