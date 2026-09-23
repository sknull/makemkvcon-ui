package de.visualdigits.makemkvconui.data.database

import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.makemkvconui.SettingsEntity
import de.visualdigits.makemkvconui.domain.model.settings.SK
import de.visualdigits.makemkvconui.domain.model.settings.Settings
import de.visualdigits.makemkvconui.domain.model.type.Language

fun Settings.toSettingsEntity(): SettingsEntity {
    val settingsEntity = SettingsEntity(
        id = 0,
        language = get<Language>(SK.language)?.localeCode ?: "en",
    )
    return settingsEntity
}

fun SettingsEntity.toSettings(): Settings {
    val newValues = valueMap(
        fieldDescriptors = Settings.DESCRIPTORS,
        values = mapOf(
            SK.language to Language.fromValue(language),
        )
    )
    return Settings().initialize(Settings.DESCRIPTORS, newValues)
}
