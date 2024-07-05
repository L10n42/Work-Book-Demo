package com.kappdev.wordbook.settings_feature.domain.repository

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import com.kappdev.wordbook.settings_feature.domain.AppSettings
import com.kappdev.wordbook.settings_feature.domain.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    @Composable
    fun <V, P> getValueAsState(settings: Settings<V, P>): State<V>

    suspend fun <V, P> getValueFlow(settings: Settings<V, P>): Flow<V>

    suspend fun getAppSettingsFlow(): Flow<AppSettings>

    suspend fun <V, P> setValueTo(settings: Settings<V, P>, value: V)

}