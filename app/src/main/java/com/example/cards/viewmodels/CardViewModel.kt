package com.example.cards.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cards.models.Card

class CardViewModel: ViewModel()  {
    private val _data: MutableLiveData<List<Card>> = MutableLiveData<List<Card>>()
    private val _loadedComplete: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val data: LiveData<List<Card>>
        get() = _data

    val loadedComplete: LiveData<Boolean>
        get() = _loadedComplete

    fun getNewShuffledData() {
        val testData = listOf(
            Card("test", 1, ""),
            Card("test", 2, ""),
            Card("test", 3, ""),
            Card("test", 4, ""),
            Card("test", 5, ""),
            Card("test", 6, ""),
            Card("test", 7, ""),
            Card("test", 8, ""),
        )

        Thread.sleep(1999)

        _data.postValue(testData.shuffled())
        _loadedComplete.postValue(true)
    }
}