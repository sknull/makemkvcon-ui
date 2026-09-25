package de.visualdigits.makemkvconui.data.database

import de.visualdigits.makemkvconui.MakemkvConUiDatabaseQueries
import de.visualdigits.makemkvconui.SettingsEntity

fun MakemkvConUiDatabaseQueries.upsertSettings(masterDataEntity: SettingsEntity) {
    val entity = getSettingsById(masterDataEntity.id).executeAsOneOrNull()
    if (entity != null) {
        updateSettings(masterDataEntity)
    } else {
        insertSettings(masterDataEntity)
    }
}

fun MakemkvConUiDatabaseQueries.insertSettings(entity: SettingsEntity) {
    insertSettings(
        language = entity.language,
        targetDirectory = entity.targetDirectory
    )
}

fun MakemkvConUiDatabaseQueries.updateSettings(entity: SettingsEntity) {
    updateSettingsEntity(
        language = entity.language,
        targetDirectory = entity.targetDirectory,
        id = entity.id
    )
}
