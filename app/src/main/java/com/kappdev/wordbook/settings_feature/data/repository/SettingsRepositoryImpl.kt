package com.kappdev.wordbook.settings_feature.data.repository

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.kappdev.wordbook.settings_feature.domain.AppSettings
import com.kappdev.wordbook.settings_feature.domain.Settings
import com.kappdev.wordbook.settings_feature.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("settings_datastore")

class SettingsRepositoryImpl @Inject constructor(
    private val context: Context
) : SettingsRepository {

    @Composable
    override fun <V, P> getValueAsState(settings: Settings<V, P>): State<V> {
        return getValueBy(settings).collectAsState(settings.default)
    }

    override suspend fun <V, P> getValueFlow(settings: Settings<V, P>): Flow<V> {
        return getValueBy(settings)
    }

    override suspend fun getAppSettingsFlow() = context.dataStore.data.map { preferences ->
        val theme = preferences.parse(Settings.Theme)
        val vibration = preferences.parse(Settings.Vibration)
        val progressBarStyle = preferences.parse(Settings.ProgressBarStyle)
        val checkForDuplication = preferences.parse(Settings.CheckForDuplication)
        val capitalizeSentences = preferences.parse(Settings.CapitalizeSentences)
        AppSettings(theme, vibration, progressBarStyle, checkForDuplication, capitalizeSentences)
    }

    private fun <V, P> getValueBy(settings: Settings <V, P>) = context.dataStore.data.map { preferences ->
        preferences.parse(settings)
    }

    private fun <V, P> Preferences.parse(settings: Settings <V, P>): V {
        val value = this[settings.key] ?: settings.converter.serialize(settings.default)
        return settings.converter.deserialize(value)
    }

    override suspend fun <V, P> setValueTo(settings: Settings<V, P>, value: V) {
        context.dataStore.edit { preferences ->
            preferences[settings.key] = settings.converter.serialize(value)
        }
    }
}