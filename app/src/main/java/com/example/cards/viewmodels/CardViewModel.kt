package com.example.cards.viewmodels

import android.app.Application
import android.content.res.Resources
import androidx.lifecycle.*
import com.bumptech.glide.load.engine.Resource
import com.example.cards.R
import com.example.cards.models.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class CardViewModel(application: Application) :  AndroidViewModel(application)  {
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
            val ctx = getApplication<Application>().applicationContext
            var iconsList = mutableListOf<Int>()

            for (i in 0..ICONS_COUNT) {
                val iconId = ctx.resources.getIdentifier("icon$i", "drawable", ctx.packageName)
                iconsList.add(iconId)
            }

            iconsList = iconsList.shuffled() as MutableList<Int>

            for (i in 1..CARDS_COUNT) {
                val randomLvl = Random.nextInt(1, 10)
                testData.add(Card(randomLvl, iconsList[i]))
            }

            testData.forEach{ card ->
                cost += card.lvl
            }

            _averageCost.postValue(cost)
            _data.postValue(testData.shuffled())
            _loadedComplete.postValue(true)
        }
    }

    companion object {
        private const val ICONS_COUNT: Int = 82
        private const val CARDS_COUNT: Int = 8
    }
}