package com.kappdev.wordbook.settings_feature.domain.converters

class LinearConverter<T>: SettingsConverter<T, T> {
    override fun serialize(value: T): T = value
    override fun deserialize(parcelable: T): T = parcelable
}