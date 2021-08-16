package com.example.cards

import android.app.Application
import com.example.cards.dependencies.ApplicationModule
import com.example.cards.dependencies.DaggerComponent
import com.example.cards.dependencies.DaggerDaggerComponent

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        daggerComponent = DaggerDaggerComponent.builder()
            .applicationModule(ApplicationModule(this))
            .build()
    }

    companion object {
        lateinit var daggerComponent: DaggerComponent
    }
}