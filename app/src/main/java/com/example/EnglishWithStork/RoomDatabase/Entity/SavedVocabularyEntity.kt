package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "saved_vocabularies",

    primaryKeys = [
        "user_id",
        "vocabulary_id"
    ],

    foreignKeys = [
        ForeignKey(
            entity = Entity_user::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = VocabularyEntity::class,
            parentColumns = ["id"],
            childColumns = ["vocabulary_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["user_id"]),
        Index(value = ["vocabulary_id"])
    ]
)
data class SavedVocabularyEntity(

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "vocabulary_id")
    val vocabularyId: Int,

    @ColumnInfo(name = "saved_at")
    val savedAt: Long = System.currentTimeMillis()
)