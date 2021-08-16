package com.example.cards.dependencies

import android.app.Application
import androidx.lifecycle.ViewModel
import com.example.cards.viewmodels.CardViewModel
import dagger.Module
import dagger.Provides

@Module(includes = [ApplicationModule::class])
class ViewModelModule {

    @Provides
    fun getViewModel(application: Application): ViewModel {
        return CardViewModel(application)
    }
}