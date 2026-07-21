package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyDao {

    @Query(
        """
        SELECT *
        FROM vocabularies
        WHERE topic_id = :topicId
        ORDER BY sort_order ASC, id ASC
        """
    )
    fun observeWordsByTopic(
        topicId: Int
    ): Flow<List<VocabularyEntity>>

    @Query(
        """
        SELECT *
        FROM vocabularies
        WHERE id = :vocabularyId
        LIMIT 1
        """
    )
    suspend fun getWordById(
        vocabularyId: Int
    ): VocabularyEntity?

    @Query(
        """
        SELECT COUNT(*)
        FROM vocabularies
        """
    )
    suspend fun countAllWords(): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM vocabularies
        WHERE topic_id = :topicId
        """
    )
    suspend fun countWordsByTopic(
        topicId: Int
    ): Int

    @Query(
        """
        SELECT *
        FROM vocabularies
        WHERE english LIKE '%' || :keyword || '%'
           OR vietnamese LIKE '%' || :keyword || '%'
        ORDER BY english ASC
        """
    )
    fun searchWords(
        keyword: String
    ): Flow<List<VocabularyEntity>>
}