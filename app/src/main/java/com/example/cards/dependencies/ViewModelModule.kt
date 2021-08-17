package com.example.cards.dependencies

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.cards.viewmodels.CardViewModel
import dagger.Module
import dagger.Provides

@Module(includes = [ApplicationModule::class])
class ViewModelModule {

    @Provides
    fun getViewModel(context: Context): ViewModel {
        return CardViewModel(context)
    }
}