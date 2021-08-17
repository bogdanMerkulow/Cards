package com.example.cards.dependencies

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    fun getContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun getApplication(): Application {
        return application
    }
}