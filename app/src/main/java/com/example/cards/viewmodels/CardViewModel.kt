package com.example.cards.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.cards.R
import com.example.cards.models.Average
import com.example.cards.models.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class CardViewModel(application: Application) :  AndroidViewModel(application)  {
    private val _data: MutableLiveData<List<Card>> = MutableLiveData<List<Card>>()
    private val _loadedComplete: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _averageCost: MutableLiveData<Average> = MutableLiveData<Average>()

    val data: LiveData<List<Card>>
        get() = _data

    val loadedComplete: LiveData<Boolean>
        get() = _loadedComplete

    val averageCost: LiveData<Average>
        get() = _averageCost

    fun getNewShuffledData() {
        viewModelScope.launch(Dispatchers.IO) {
            val testData = mutableListOf<Card>()
            val ctx = getApplication<Application>().applicationContext
            var iconsList = mutableListOf<Int>()
            val average = Average(0, 0)

            for (i in 0..ICONS_COUNT) {
                val iconId = ctx.resources.getIdentifier("icon$i", "drawable", ctx.packageName)
                iconsList.add(iconId)
            }

            iconsList = iconsList.shuffled() as MutableList<Int>

            for (i in 1..CARDS_COUNT) {
                val cardLvl = Random.nextInt(MIN_LVL_RARE, MAX_LVL_RARE)

                val elixir = getRare(cardLvl)

                testData.add(Card(elixir, cardLvl, iconsList[i]))
            }

            testData.forEach{ card ->
                average.cost += card.lvl
            }

            average.cost = average.cost / CARDS_COUNT

            average.elixir = getRare(average.cost)

            _averageCost.postValue(average)
            _data.postValue(testData.shuffled())
            _loadedComplete.postValue(true)
        }
    }

    private fun getRare(value: Int): Int {
        return when(value) {
            in 1..2 -> R.drawable.elixir_common
            in 3..4 -> R.drawable.elixir_rare
            in 5..6 -> R.drawable.elixir_epic
            in 7..8 -> R.drawable.elixir_legendary
            else -> R.drawable.elixir_common
        }
    }

    companion object {
        private const val ICONS_COUNT: Int = 82
        private const val CARDS_COUNT: Int = 8
        private const val MIN_LVL_RARE: Int = 1
        private const val MAX_LVL_RARE: Int = 10
    }
}