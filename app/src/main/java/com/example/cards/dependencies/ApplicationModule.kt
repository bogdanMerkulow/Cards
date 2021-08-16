package com.example.cards.dependencies

import android.app.Application
import dagger.Module
import dagger.Provides

@Module
class ApplicationModule(private val application: Application) {

    @Provides
    fun getApplication(): Application {
        return application
    }
}