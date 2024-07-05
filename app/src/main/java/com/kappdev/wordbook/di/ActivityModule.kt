package com.kappdev.wordbook.di

import android.content.Context
import androidx.room.Room
import com.kappdev.wordbook.core.data.data_rource.DictionaryDatabase
import com.kappdev.wordbook.core.data.repository.CardRepositoryImpl
import com.kappdev.wordbook.core.data.repository.CollectionRepositoryImpl
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.main_feature.data.repository.SampleDataProviderImpl
import com.kappdev.wordbook.main_feature.domain.repository.SampleDataProvider
import com.kappdev.wordbook.main_feature.domain.repository.StorageRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Named

@Module
@InstallIn(ActivityComponent::class)
object ActivityModule {

    @Provides
    @Named("ActivityDatabase")
    @ActivityScoped
    fun provideDictionaryDatabase(@ApplicationContext context: Context): DictionaryDatabase {
        return Room.databaseBuilder(context, DictionaryDatabase::class.java, DictionaryDatabase.NAME).build()
    }

    @Provides
    @Named("ActivityCollectionRepository")
    @ActivityScoped
    fun provideCollectionRepository(@Named("ActivityDatabase") db: DictionaryDatabase): CollectionRepository {
        return CollectionRepositoryImpl(db.getCollectionDao())
    }

    @Provides
    @Named("ActivityCardRepository")
    @ActivityScoped
    fun provideCardRepository(@Named("ActivityDatabase") db: DictionaryDatabase): CardRepository {
        return CardRepositoryImpl(db.getCardDao())
    }

    @Provides
    @ActivityScoped
    fun provideSampleDataProvider(
        @Named("ActivityCardRepository") cardRepository: CardRepository,
        @Named("ActivityCollectionRepository") collectionRepository: CollectionRepository,
        storageRepository: StorageRepository
    ): SampleDataProvider {
        return SampleDataProviderImpl(collectionRepository, cardRepository, storageRepository)
    }
}