package de.visualdigits.makemkvconui.data.database

import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.makemkvconui.SettingsEntity
import de.visualdigits.makemkvconui.domain.model.settings.SK
import de.visualdigits.makemkvconui.domain.model.settings.Settings
import de.visualdigits.makemkvconui.domain.model.type.Language
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

fun Settings.toSettingsEntity(): SettingsEntity {
    val get = get<Language>(SK.language)
    val get1 = get<Path>(SK.targetDirectory)
    val settingsEntity = SettingsEntity(
        id = 0,
        language = get?.localeCode ?: "en",
        targetDirectory = get1?.let { SystemFileSystem.resolve(it).toString() }
    )
    return settingsEntity
}

fun SettingsEntity.toSettings(): Settings {
    val newValues = valueMap(
        fieldDescriptors = Settings.DESCRIPTORS,
        values = mapOf(
            SK.language to Language.fromValue(language),
            SK.targetDirectory to targetDirectory?.let { v -> Path(v) }
        )
    )
    return Settings().initialize(Settings.DESCRIPTORS, newValues)
}
