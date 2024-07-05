package com.kappdev.wordbook.di

import android.content.Context
import com.kappdev.wordbook.core.data.repository.FirstAppLaunchRecorderImpl
import com.kappdev.wordbook.core.domain.repository.FirstAppLaunchRecorder
import com.kappdev.wordbook.core.domain.util.TextToSpeechHelper
import com.kappdev.wordbook.main_feature.data.repository.CardsOrderSaverImpl
import com.kappdev.wordbook.main_feature.data.repository.CollectionsOrderSaverImpl
import com.kappdev.wordbook.main_feature.data.repository.StorageRepositoryImpl
import com.kappdev.wordbook.main_feature.domain.repository.CardsOrderSaver
import com.kappdev.wordbook.main_feature.domain.repository.CollectionsOrderSaver
import com.kappdev.wordbook.main_feature.domain.repository.StorageRepository
import com.kappdev.wordbook.reminders_feature.data.AppUsageRecorderImpl
import com.kappdev.wordbook.reminders_feature.domain.AppUsageRecorder
import com.kappdev.wordbook.settings_feature.data.repository.SettingsRepositoryImpl
import com.kappdev.wordbook.settings_feature.domain.repository.SettingsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideStorageRepository(@ApplicationContext context: Context): StorageRepository {
        return StorageRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideTextToSpeechHelper(@ApplicationContext context: Context): TextToSpeechHelper {
        return TextToSpeechHelper(context)
    }

    @Provides
    @Singleton
    fun provideSettingsRepository(@ApplicationContext context: Context): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideCollectionsOrderSaver(@ApplicationContext context: Context): CollectionsOrderSaver {
        return CollectionsOrderSaverImpl(context)
    }

    @Provides
    @Singleton
    fun provideCardsOrderSaver(@ApplicationContext context: Context): CardsOrderSaver {
        return CardsOrderSaverImpl(context)
    }

    @Provides
    @Singleton
    fun provideAppUsageRecorder(@ApplicationContext context: Context): AppUsageRecorder {
        return AppUsageRecorderImpl(context)
    }

    @Provides
    @Singleton
    fun provideFirstAppLaunchRecorder(@ApplicationContext context: Context): FirstAppLaunchRecorder {
        return FirstAppLaunchRecorderImpl(context)
    }
}