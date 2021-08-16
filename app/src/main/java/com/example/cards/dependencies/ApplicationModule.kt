package com.example.cards.dependencies

import android.app.Application
import dagger.Module

@Module
class ApplicationModule(private val application: Application) {
    fun getApplication(): Application = application
}