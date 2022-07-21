package com.example.casino.app

import android.app.Application
import com.example.casino.app.di.*
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        lateinit var instance: App

        fun getString(resId: Int): String {
            return instance.getString(resId)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        startKoin {
            modules(
                viewModelModule,
                networkModule,
                oddsApiModule,
                repositoryModule
            )
        }
    }
}