package com.kappdev.wordbook.di

import android.content.Context
import androidx.room.Room
import com.kappdev.wordbook.core.data.data_rource.DictionaryDatabase
import com.kappdev.wordbook.core.data.repository.CardRepositoryImpl
import com.kappdev.wordbook.core.data.repository.CollectionRepositoryImpl
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object DatabaseModule {

    @Provides
    @ViewModelScoped
    fun provideDictionaryDatabase(@ApplicationContext context: Context): DictionaryDatabase {
        return Room.databaseBuilder(context, DictionaryDatabase::class.java, DictionaryDatabase.NAME).build()
    }

    @Provides
    @ViewModelScoped
    fun provideCollectionRepository(db: DictionaryDatabase): CollectionRepository {
        return CollectionRepositoryImpl(db.getCollectionDao())
    }

    @Provides
    @ViewModelScoped
    fun provideCardRepository(db: DictionaryDatabase): CardRepository {
        return CardRepositoryImpl(db.getCardDao())
    }

}