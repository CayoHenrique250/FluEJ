package com.example.fluej.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.example.fluej.data.api.ApiCache
import com.example.fluej.data.api.ApiCacheDao

@Database(
    entities = [
        Member::class,
        Activity::class,
        Project::class,
        ProjectMember::class,
        ApiCache::class
    ],
    version = 7,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun memberDao(): MemberDao
    abstract fun activityDao(): ActivityDao
    abstract fun projectDao(): ProjectDao
    abstract fun apiCacheDao(): ApiCacheDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `api_cache` (
                        `chave` TEXT NOT NULL,
                        `json` TEXT NOT NULL,
                        `atualizadoEm` INTEGER NOT NULL,
                        PRIMARY KEY(`chave`)
                    )
                    """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fluej_database"
                )
                    .addMigrations(MIGRATION_6_7)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
