package com.upakon.moonlog

import android.app.Application
import com.upakon.moonlog.di.dataModule
import com.upakon.moonlog.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * [MoonLog] Application class
 * This class will create the modules to inject through Koin
 */

class MoonLog : Application() {

    override fun onCreate() {
        super.onCreate()

        //starting Koin
        startKoin {
            androidContext(this@MoonLog)
            //add all the modules needed
            modules(
                dataModule,
                viewModelModule
            )
        }
    }

}