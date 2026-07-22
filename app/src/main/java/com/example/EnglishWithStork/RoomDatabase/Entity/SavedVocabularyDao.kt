package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedVocabularyDao {

    /**
     * IGNORE giúp không bị crash nếu người dùng bấm lưu
     * một từ đã có trong sổ tay.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertSavedVocabulary(
        savedVocabulary: SavedVocabularyEntity
    ): Long

    /**
     * Xóa một từ khỏi sổ tay của đúng người dùng.
     */
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

    /**
     * Lấy ID các từ đã lưu để đổi icon bookmark.
     */
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

    /**
     * Lấy đầy đủ dữ liệu các từ trong sổ tay.
     */
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