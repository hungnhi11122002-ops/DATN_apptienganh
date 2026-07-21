package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "vocabularies",

    foreignKeys = [
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],

    indices = [
        Index(value = ["topic_id"])
    ]
)
data class VocabularyEntity(

    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "topic_id")
    val topicId: Int,

    val english: String,

    @ColumnInfo(name = "word_class")
    val wordClass: String? = null,

    val vietnamese: String,

    val phonetic: String? = null,

    @ColumnInfo(name = "example_en")
    val exampleEnglish: String? = null,

    @ColumnInfo(name = "example_vi")
    val exampleVietnamese: String? = null,

    @ColumnInfo(name = "image_name")
    val imageName: String? = null,

    @ColumnInfo(name = "audio_name")
    val audioName: String? = null,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int
)