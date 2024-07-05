package com.kappdev.wordbook.core.data.data_rource

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kappdev.wordbook.core.domain.converter.ColorConverter
import com.kappdev.wordbook.core.domain.converter.LocaleConverter
import com.kappdev.wordbook.core.domain.model.Card
import com.kappdev.wordbook.core.domain.model.Collection

@Database(
    entities = [Card::class, Collection::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ],
    exportSchema = true
)
@TypeConverters(LocaleConverter::class, ColorConverter::class)
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun getCardDao(): CardDao
    abstract fun getCollectionDao(): CollectionDao

    companion object {

        const val NAME = "dictionary_database"

        private const val SUFFIX_SHM = "-shm"
        private const val SUFFIX_WAL = "-wal"

        private const val DATABASE_SHM = NAME + SUFFIX_SHM
        private const val DATABASE_WAL = NAME + SUFFIX_WAL

        val dbFileNames = listOf(NAME, DATABASE_SHM, DATABASE_WAL)

        fun getFilePaths(context: Context): List<String> {
            val dbPath = context.getDatabasePath(NAME).path
            val dbShm = dbPath + SUFFIX_SHM
            val dbWal = dbPath + SUFFIX_WAL
            return listOf(dbPath, dbShm, dbWal)
        }
    }

}