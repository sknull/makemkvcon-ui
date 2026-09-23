package de.visualdigits.makemkvconui

import android.app.Application
import co.touchlab.kermit.Logger
import de.visualdigits.makemkvconui.di.platformModule
import de.visualdigits.makemkvconui.di.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.GlobalContext.startKoin

class MakemkvConUiApplication: Application() {

    override fun onCreate() {
        Logger.i("Starting koin...")
        startKoin {
            androidContext(this@MakemkvConUiApplication)
            workManagerFactory()
            modules(sharedModule, platformModule)
        }

        // IMPORTANT do super create AFTER koin initializing to avoid problems with work managers
        Logger.i("Initializing application...")
        super.onCreate()

        Logger.i("Application initialized")
    }
}
