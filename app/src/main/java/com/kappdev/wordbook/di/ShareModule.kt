package com.kappdev.wordbook.di

import android.content.Context
import com.kappdev.wordbook.core.domain.repository.CardRepository
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import com.kappdev.wordbook.share_feature.data.repository.ShareCollectionAsPDFImpl
import com.kappdev.wordbook.share_feature.data.repository.ShareCollectionImpl
import com.kappdev.wordbook.share_feature.domain.repository.ShareCollection
import com.kappdev.wordbook.share_feature.domain.repository.ShareCollectionAsPDF
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ShareModule {

    @Provides
    @ViewModelScoped
    fun provideShareCollectionAsPDF(
        collectionRepository: CollectionRepository,
        cardRepository: CardRepository,
        @ApplicationContext context: Context
    ): ShareCollectionAsPDF {
        return ShareCollectionAsPDFImpl(collectionRepository, cardRepository, context)
    }

    @Provides
    @ViewModelScoped
    fun provideShareCollection(
        collectionRepository: CollectionRepository,
        cardRepository: CardRepository,
        @ApplicationContext context: Context
    ): ShareCollection {
        return ShareCollectionImpl(context, collectionRepository, cardRepository)
    }

}