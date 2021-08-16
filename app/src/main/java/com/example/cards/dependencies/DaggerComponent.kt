package com.example.cards.dependencies

import androidx.lifecycle.ViewModel
import dagger.Component
import dagger.Provides

@Component(modules = [ApplicationModule::class])
interface DaggerComponent {

    @Provides
    fun getViewModel(): ViewModel
}