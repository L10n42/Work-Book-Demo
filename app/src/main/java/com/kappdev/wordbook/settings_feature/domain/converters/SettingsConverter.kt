package com.kappdev.wordbook.settings_feature.domain.converters

interface SettingsConverter<V, P> {
    fun serialize(value: V): P
    fun deserialize(parcelable: P): V
}