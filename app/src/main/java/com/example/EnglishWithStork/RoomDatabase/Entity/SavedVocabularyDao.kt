package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedVocabularyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSavedVocabulary(
        savedVocabulary: SavedVocabularyEntity
    ): Long


    @Query(
        """
        DELETE FROM saved_vocabularies
        WHERE user_id = :userId
        AND vocabulary_id = :vocabularyId
        """
    )
    suspend fun deleteSavedVocabulary(
        userId: Int,
        vocabularyId: Int
    )

    @Query(
        """
        SELECT vocabulary_id
        FROM saved_vocabularies
        WHERE user_id = :userId
        """
    )
    fun observeSavedVocabularyIds(
        userId: Int
    ): Flow<List<Int>>

    @Query(
        """
        SELECT vocabularies.*
        FROM vocabularies
        INNER JOIN saved_vocabularies
            ON vocabularies.id =
               saved_vocabularies.vocabulary_id
        WHERE saved_vocabularies.user_id = :userId
        ORDER BY saved_vocabularies.saved_at DESC
        """
    )
    fun observeSavedVocabularies(
        userId: Int
    ): Flow<List<VocabularyEntity>>
}