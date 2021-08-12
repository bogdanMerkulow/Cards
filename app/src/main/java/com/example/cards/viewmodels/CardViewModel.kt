package com.example.cards.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cards.models.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class CardViewModel: ViewModel()  {
    private val _data: MutableLiveData<List<Card>> = MutableLiveData<List<Card>>()
    private val _loadedComplete: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _averageCost: MutableLiveData<Int> = MutableLiveData<Int>()

    val data: LiveData<List<Card>>
        get() = _data

    val loadedComplete: LiveData<Boolean>
        get() = _loadedComplete

    val averageCost: LiveData<Int>
        get() = _averageCost

    fun getNewShuffledData() {
        viewModelScope.launch(Dispatchers.IO) {
            val testData = mutableListOf<Card>()
            var cost = 0

            for (i in 1..8) {
                val randomLvl = Random.nextInt(1, 10)
                testData.add(Card(randomLvl, ""))
            }

            testData.forEach{ card ->
                cost += card.lvl
            }

            Thread.sleep(1000)

            _averageCost.postValue(cost)
            _data.postValue(testData.shuffled())
            _loadedComplete.postValue(true)
        }
    }
}