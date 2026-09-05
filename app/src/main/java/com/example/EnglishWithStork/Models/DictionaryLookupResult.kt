package com.example.EnglishWithStork.Models

data class DictionaryLookupResult(

    val english: String,
    val phonetic: String? = null,
    val wordClass: String? = null,
    val vietnamese: String? = null,
    val definition: String? = null,
    val exampleEnglish: String? = null,
    val exampleVietnamese: String? = null,

    /**
     * Có giá trị => từ này nằm trong Room.
     * null => từ lấy từ Internet.
     *
     * Không tạo ID giả cho từ online.
     */
    val localVocabularyId: Int? = null
)


sealed class DictionaryLookupState {

    data class Found(
        val result: DictionaryLookupResult
    ) : DictionaryLookupState()


    /**
     * Không có trong Room,
     * nhưng chưa được phép gọi API.
     *
     * Dùng trong lúc user đang gõ.
     */
    object OfflineMiss : DictionaryLookupState()


    /**
     * Room không có và API cũng không có.
     */
    object NotFound : DictionaryLookupState()


    /**
     * Lỗi mạng hoặc lỗi server.
     */
    data class Error(
        val message: String
    ) : DictionaryLookupState()
}