package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {

    @Query(
        """
        SELECT *
        FROM topics
        ORDER BY sort_order ASC, id ASC
        """
    )
    fun observeAllTopics(): Flow<List<TopicEntity>>

    @Query(
        """
        SELECT *
        FROM topics
        WHERE id = :topicId
        LIMIT 1
        """
    )
    suspend fun getTopicById(
        topicId: Int
    ): TopicEntity?

    @Query(
        """
        SELECT COUNT(*)
        FROM topics
        """
    )
    suspend fun countAllTopics(): Int

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
}