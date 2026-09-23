package de.visualdigits.makemkvconui.data.repository

import co.touchlab.kermit.Logger
import de.visualdigits.common.domain.model.configuration.AbstractConfiguration.Companion.valueMap
import de.visualdigits.common.domain.model.errorhandling.Result
import de.visualdigits.common.domain.util.CryptoBox
import de.visualdigits.makemkvconui.MakemkvConUiDatabaseQueries
import de.visualdigits.makemkvconui.data.database.toSettings
import de.visualdigits.makemkvconui.data.database.toSettingsEntity
import de.visualdigits.makemkvconui.data.database.upsertSettings
import de.visualdigits.makemkvconui.domain.model.errorhandling.DataError
import de.visualdigits.makemkvconui.domain.model.settings.SK
import de.visualdigits.makemkvconui.domain.model.settings.Settings
import de.visualdigits.makemkvconui.domain.model.type.Language
import de.visualdigits.makemkvconui.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readString
import kotlinx.io.writeString
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonPrimitive

class DefaultSettingsRepository(
    private val dao: MakemkvConUiDatabaseQueries,
    private val cryptoBox: CryptoBox
): SettingsRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO

    override suspend fun getSettings(): Result<Settings?, DataError .Local> = withContext(dispatcher) {
        try {
            dao.getSettingsById(0)
                .executeAsOneOrNull()
                ?.let { settingsEntity ->
                    settingsEntity
                        .toSettings()
                        .let { s ->
                            Result.Success(s)
                        }
                } ?: Result.Success(null)
        } catch (e: Exception) {
            Logger.e("Could not load settings", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun setSettings(settings: Settings): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val settingsEntity = settings.toSettingsEntity()
            dao.upsertSettings(settingsEntity)
            Result.Success(Unit)
        } catch (e: Exception) {
            Logger.e("Could not set settings", e)
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun importSettings(source: Source): Result<Settings, DataError.Local> = withContext(dispatcher) {
        try {
            val jsonMapper = Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            }
            val json = source.use { ins ->
                ins.readString()
            }

            val newValues = valueMap(
                fieldDescriptors = Settings.DESCRIPTORS,
                values = jsonMapper
                    .decodeFromString<Map<String, JsonElement>>(json)
                    .mapNotNull { (key, value) ->
                        val sk = SK.fromString(key)
                        if (sk != null) {
                            val rawValue = value.jsonPrimitive.content
//                            val finalValue = if (sk == SK.aisstreamApiKey) {
//                                cryptoBox.decrypt(rawValue)
//                            } else {
//                                rawValue
//                            }
//                            Pair(sk, finalValue)
                            Pair(sk, rawValue)
                        } else {
                            null
                        }
                    }
                    .toMap()
            )
            val settings = Settings().initialize(Settings.DESCRIPTORS, newValues)
            setSettings(settings)
            Result.Success(settings)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }

    override suspend fun exportSettings(settings: Settings, sink: Sink): Result<Unit, DataError.Local> = withContext(dispatcher) {
        try {
            val jsonMapper = Json {
                prettyPrint = true
            }
            val value = settings.toSettingsRepositoryEntity(cryptoBox)
            val json = jsonMapper.encodeToString(value)
            sink.use { writer ->
                writer.writeString(json)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN, e)
        }
    }
}

private fun Settings.toSettingsRepositoryEntity(cryptoBox: CryptoBox): SettingsRepositoryEntity {
    val settingsEntity = SettingsRepositoryEntity(
        id = 0,
        language = get<Language>(SK.language)?.name ?: "EN",
    )
    return settingsEntity
}

@Serializable
private data class SettingsRepositoryEntity(
    val id: Long,
    val language: String,
)
