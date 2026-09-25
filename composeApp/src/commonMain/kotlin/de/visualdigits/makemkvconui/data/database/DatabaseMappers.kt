package de.visualdigits.makemkvconui.data.database

import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.makemkvconui.SettingsEntity
import de.visualdigits.makemkvconui.domain.model.settings.SK
import de.visualdigits.makemkvconui.domain.model.settings.Settings
import de.visualdigits.makemkvconui.domain.model.type.Language
import java.io.File

fun Settings.toSettingsEntity(): SettingsEntity {
    val settingsEntity = SettingsEntity(
        id = 0,
        language = get<Language>(SK.language)?.localeCode ?: "en",
        targetDirectory = get<File>(SK.targetDirectory)?.canonicalPath
    )
    return settingsEntity
}

fun SettingsEntity.toSettings(): Settings {
    val newValues = valueMap(
        fieldDescriptors = Settings.DESCRIPTORS,
        values = mapOf(
            SK.language to Language.fromValue(language),
            SK.targetDirectory to targetDirectory?.let { File(it) }
        )
    )
    return Settings().initialize(Settings.DESCRIPTORS, newValues)
}
