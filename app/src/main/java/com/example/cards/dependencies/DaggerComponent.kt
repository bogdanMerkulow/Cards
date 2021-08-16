package com.example.cards.dependencies

import androidx.lifecycle.ViewModel
import dagger.Component

@Component(modules = [ViewModelModule::class])
interface DaggerComponent {
    fun getViewModel(): ViewModel
}