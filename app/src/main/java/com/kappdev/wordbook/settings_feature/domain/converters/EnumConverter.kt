package com.kappdev.wordbook.settings_feature.domain.converters

class EnumConverter<E : Enum<E>>(private val enumClass: Class<E>) : SettingsConverter<E, String> {
    override fun serialize(value: E): String {
        return value.name
    }

    override fun deserialize(parcelable: String): E {
        return enumClass.enumConstants?.firstOrNull { it.name == parcelable }
            ?: throw IllegalArgumentException("Enum constant $parcelable not found")
    }
}