package com.example.EnglishWithStork.RoomDatabase.Entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "table_users",
        indices = [Index( value = ["tendangnhap"], unique = true)]
)
data class Entity_user(
        @PrimaryKey(autoGenerate = true)
        val id: Int=0,
        val tendangnhap: String,
        val matkhau: String,
        val ngaysinh: String,
        val gioitinh: String
)
