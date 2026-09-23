package de.visualdigits.makemkvconui.di

import app.cash.sqldelight.ColumnAdapter
import de.visualdigits.common.domain.util.CryptoBox
import de.visualdigits.common.domain.util.EncryptedString
import de.visualdigits.makemkvconui.MakemkvConUiDatabaseQueries
import de.visualdigits.makemkvconui.data.database.DriverFactory
import de.visualdigits.makemkvconui.data.repository.DefaultSettingsRepository
import de.visualdigits.makemkvconui.domain.repository.SettingsRepository
import de.visualdigits.makemkvconui.presentation.model.MakemkvConUiViewModel
import de.visualdigits.makemkvconui.SettingsDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

expect val homeDirectory: String


val sharedModule = module {

    single(named("homeDirectory")) { homeDirectory }

    single { CoroutineScope(SupervisorJob() + Dispatchers.Default) }

    singleOf(::MakemkvConUiViewModel)

    single {
        val driver = get<DriverFactory>().createDriver(get<String>(named("homeDirectory")))
        val cryptoBox = get<CryptoBox>()

        val passwordAdapter = object : ColumnAdapter<EncryptedString, String> {
            override fun decode(databaseValue: String): EncryptedString = cryptoBox.decrypt(databaseValue)
            override fun encode(value: EncryptedString): String = cryptoBox.encrypt(value)
        }

        SettingsDatabase(
            driver,
        )
    }

    single<MakemkvConUiDatabaseQueries> {
        get<SettingsDatabase>().makemkvConUiDatabaseQueries
    }

    singleOf(::DefaultSettingsRepository).bind<SettingsRepository>()
}
