package com.example.eyelyze

import android.app.Application
import com.example.eyelyze.di.feature.historyModule
import com.example.eyelyze.di.feature.viewModelModule
import com.example.eyelyze.di.localModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class BaseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger(Level.NONE)
            androidContext(this@BaseApp)
            modules(
                listOf(
//                    networkModule,
                    localModule,
//                    preferenceModule,
//                    authModule,
                    historyModule,
                    viewModelModule
                )
            )
        }
    }
}