package com.kappdev.wordbook.di

import android.content.Context
import com.kappdev.wordbook.backup_feature.data.repository.BackupRepositoryImpl
import com.kappdev.wordbook.backup_feature.domain.repository.BackupRepository
import com.kappdev.wordbook.core.domain.repository.CollectionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object BackupModule {

    @Provides
    @ViewModelScoped
    fun provideBackupRepository(
        @ApplicationContext context: Context,
        collectionRepository: CollectionRepository
    ): BackupRepository {
        return BackupRepositoryImpl(context, collectionRepository)
    }

}