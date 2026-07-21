
package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "topics")
data class TopicEntity(

    @PrimaryKey
    val id: Int,

    val name: String,

    @ColumnInfo(name = "image_name")
    val imageName: String,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int
)