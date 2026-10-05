package com.plcoding.marky

import android.app.Application
import com.plcoding.marky.di.initKoin
import org.koin.android.ext.koin.androidContext

class MarkyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MarkyApplication)
        }
    }
}
