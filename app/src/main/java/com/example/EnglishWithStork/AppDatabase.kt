package com.example.EnglishWithStork.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.EnglishWithStork.RoomDatabase.Entity.DAO
import com.example.EnglishWithStork.RoomDatabase.Entity.Entity_user

@Database(
    entities = [Entity_user::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): DAO
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? =  null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "english_with_stork_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}