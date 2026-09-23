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

fun MakemkvConUiDatabaseQueries.insertSettings(masterDataEntity: SettingsEntity) {
    insertSettings(
        language = masterDataEntity.language,
    )
}

fun MakemkvConUiDatabaseQueries.updateSettings(masterDataEntity: SettingsEntity) {
    updateSettingsEntity(
        language = masterDataEntity.language,
        id = masterDataEntity.id
    )
}
