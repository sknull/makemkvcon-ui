package de.visualdigits.makemkvconui.domain.repository

import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.makemkvconui.domain.model.errorhandling.DataError
import de.visualdigits.makemkvconui.domain.model.settings.Settings
import kotlinx.io.Sink
import kotlinx.io.Source

interface SettingsRepository {

    suspend fun getSettings(): Result<Settings?, DataError.Local>

    suspend fun setSettings(settings: Settings): Result<Unit, DataError.Local>

    suspend fun importSettings(ins: Source): Result<Settings, DataError.Local>

    suspend fun exportSettings(settings: Settings, outs: Sink): Result<Unit, DataError.Local>
}
