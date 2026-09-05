package com.example.EnglishWithStork.network

import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit


// =====================================================
// FREE DICTIONARY API
// =====================================================

data class DictionaryEntryDto(

    val word: String? = null,

    val phonetic: String? = null,

    val phonetics: List<PhoneticDto>? = null,

    val meanings: List<MeaningDto>? = null
)


data class PhoneticDto(

    val text: String? = null,

    val audio: String? = null
)


data class MeaningDto(

    val partOfSpeech: String? = null,

    val definitions: List<DefinitionDto>? = null
)


data class DefinitionDto(

    val definition: String? = null,

    val example: String? = null,

    val synonyms: List<String>? = null,

    val antonyms: List<String>? = null
)


interface FreeDictionaryApi {

    @GET("api/v2/entries/en/{word}")
    suspend fun lookupWord(

        @Path("word")
        word: String

    ): Response<List<DictionaryEntryDto>>
}


// =====================================================
// DATAMUSE API
// =====================================================

data class DatamuseWordDto(

    val word: String? = null,

    val defs: List<String>? = null,

    val tags: List<String>? = null
)


interface DatamuseApi {

    @GET("words?md=dpr&ipa=1&max=1")
    suspend fun lookupWord(

        @Query("sp")
        word: String

    ): Response<List<DatamuseWordDto>>
}


// =====================================================
// RETROFIT CLIENTS
// =====================================================

object DictionaryNetwork {


    private val httpClient =
        OkHttpClient
            .Builder()

            .addInterceptor { chain ->

                val request =
                    chain.request()

                android.util.Log.d(
                    "DICTIONARY_URL",
                    request.url.toString()
                )

                chain.proceed(request)
            }

            .protocols(
                listOf(
                    Protocol.HTTP_1_1
                )
            )

            .connectTimeout(
                10,
                TimeUnit.SECONDS
            )

            .readTimeout(
                12,
                TimeUnit.SECONDS
            )

            .writeTimeout(
                10,
                TimeUnit.SECONDS
            )

            .retryOnConnectionFailure(true)

            .build()


    // =================================================
    // DATAMUSE
    // =================================================

    val datamuseApi: DatamuseApi by lazy {

        Retrofit
            .Builder()

            .baseUrl(
                "https://api.datamuse.com/"
            )

            .client(
                httpClient
            )

            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()

            .create(
                DatamuseApi::class.java
            )
    }


    // =================================================
    // FREE DICTIONARY - FALLBACK
    // =================================================

    val dictionaryApi: FreeDictionaryApi by lazy {

        Retrofit
            .Builder()

            .baseUrl(
                "https://api.dictionaryapi.dev/"
            )

            .client(
                httpClient
            )

            .addConverterFactory(
                GsonConverterFactory.create()
            )

            .build()

            .create(
                FreeDictionaryApi::class.java
            )
    }
}