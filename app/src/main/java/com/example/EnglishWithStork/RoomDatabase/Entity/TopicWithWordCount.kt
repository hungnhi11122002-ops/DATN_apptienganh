package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.ColumnInfo

data class TopicWithWordCount(

    val id: Int,

    val name: String,

    @ColumnInfo(name = "image_name")
    val imageName: String,

    @ColumnInfo(name = "word_count")
    val wordCount: Int
)