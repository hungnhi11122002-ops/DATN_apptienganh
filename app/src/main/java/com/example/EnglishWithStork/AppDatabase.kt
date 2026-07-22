package com.example.EnglishWithStork.RoomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.EnglishWithStork.RoomDatabase.Entity.DAO
import com.example.EnglishWithStork.RoomDatabase.Entity.Entity_user
import com.example.EnglishWithStork.RoomDatabase.Entity.SavedVocabularyDao
import com.example.EnglishWithStork.RoomDatabase.Entity.SavedVocabularyEntity
import com.example.EnglishWithStork.RoomDatabase.Entity.TopicDao
import com.example.EnglishWithStork.RoomDatabase.Entity.TopicEntity
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyDao
import com.example.EnglishWithStork.RoomDatabase.Entity.VocabularyEntity

@Database(
    entities = [
        Entity_user::class,
        TopicEntity::class,
        VocabularyEntity::class,
        SavedVocabularyEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): DAO

    abstract fun topicDao(): TopicDao

    abstract fun vocabularyDao(): VocabularyDao

    abstract fun savedVocabularyDao(): SavedVocabularyDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 =
            object : Migration(2, 3) {

                override fun migrate(
                    database: SupportSQLiteDatabase
                ) {
                    database.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `saved_vocabularies` (
                            `user_id` INTEGER NOT NULL,
                            `vocabulary_id` INTEGER NOT NULL,
                            `saved_at` INTEGER NOT NULL,
                            PRIMARY KEY(
                                `user_id`,
                                `vocabulary_id`
                            ),
                            FOREIGN KEY(`user_id`)
                                REFERENCES `table_users`(`id`)
                                ON UPDATE NO ACTION
                                ON DELETE CASCADE,
                            FOREIGN KEY(`vocabulary_id`)
                                REFERENCES `vocabularies`(`id`)
                                ON UPDATE NO ACTION
                                ON DELETE CASCADE
                        )
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_saved_vocabularies_user_id`
                        ON `saved_vocabularies` (`user_id`)
                        """.trimIndent()
                    )

                    database.execSQL(
                        """
                        CREATE INDEX IF NOT EXISTS
                        `index_saved_vocabularies_vocabulary_id`
                        ON `saved_vocabularies`
                        (`vocabulary_id`)
                        """.trimIndent()
                    )
                }
            }

        fun getDatabase(
            context: Context
        ): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "english_with_stork_database"
                )
                    .createFromAsset(
                        "database/english_with_stork.db"
                    )
                    .addMigrations(MIGRATION_2_3)
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}