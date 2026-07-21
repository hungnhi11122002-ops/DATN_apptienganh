package com.example.EnglishWithStork.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.EnglishWithStork.RoomDatabase.Entity.DAO
import com.example.EnglishWithStork.RoomDatabase.Entity.Entity_user
import com.example.EnglishWithStork.RoomDatabase.Entity.TopicDao
import com.example.EnglishWithStork.RoomDatabase.Entity.TopicEntity
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyDao
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity

@Database(
    entities = [Entity_user::class,
                TopicEntity::class,
                VocabularyEntity::class],

    version = 2,
    exportSchema = true
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): DAO
    abstract  fun topicDao(): TopicDao
    abstract  fun vocabularyDao(): VocabularyDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? =  null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "english_with_stork_database"
                ).createFromAsset("database/english_with_stork.db"
                ).createFromAsset("database/english_with_stork.db"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}