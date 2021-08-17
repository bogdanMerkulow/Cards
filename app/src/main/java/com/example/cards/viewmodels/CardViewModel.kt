package com.example.cards.viewmodels

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.*
import com.example.cards.R
import com.example.cards.models.Average
import com.example.cards.models.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class CardViewModel(private val context: Context) : ViewModel() {
    private val _data: MutableLiveData<List<Card>> = MutableLiveData<List<Card>>()
    private val _loadedComplete: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _averageCost: MutableLiveData<Average> = MutableLiveData<Average>()
    private val _readyToNewData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    val data: LiveData<List<Card>>
        get() = _data

    val loadedComplete: LiveData<Boolean>
        get() = _loadedComplete

    val averageCost: LiveData<Average>
        get() = _averageCost

    val readyToNewData: LiveData<Boolean>
        get() = _readyToNewData

    fun getNewShuffledData() {
        viewModelScope.launch(Dispatchers.IO) {
            val cards = mutableListOf<Card>()
            var iconsList = mutableListOf<Int>()
            val average = Average(0, 0, 0)

            _readyToNewData.postValue(false)


            getRandomNumbersList(CARDS_COUNT, ICONS_COUNT).forEach { randomNumber ->
                val iconId = context.resources.getIdentifier(
                        "$ICON_PREFIX$randomNumber",
                                DEF_TYPE,
                                context.packageName
                        )

                iconsList.add(iconId)
            }

            iconsList = iconsList.shuffled() as MutableList<Int>

            iconsList.forEach { icon ->
                val cardLvl = Random.nextInt(MIN_LVL_RARE, MAX_LVL_RARE)

                val elixir = getRare(cardLvl)

                cards.add(Card(elixir, cardLvl, icon))
            }

            cards.forEach { card ->
                average.cost += card.lvl
            }

            average.cost = average.cost / CARDS_COUNT
            average.elixir = getRare(average.cost)
            average.rareColor = getRareColor(average.cost)

            Thread.sleep(START_DELAY)

            _data.postValue(cards.shuffled())
            _loadedComplete.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                Thread.sleep(TIME_TO_END_ANIMATION)
                _averageCost.postValue(average)
                _readyToNewData.postValue(true)
            }
        }
    }

    private fun getRandomNumbersList(cardsCount: Int, iconsCount: Int): List<Int> {
        val numberList = mutableListOf<Int>()

        for (i in (iconsCount - cardsCount)..iconsCount) {
            val number = Random.nextInt(1, i)

            if (numberList.contains(number))
                numberList.add(i)
            else
                numberList.add(number)

            if (numberList.count() > cardsCount) {
                return numberList.shuffled()
            }
        }
        return listOf()
    }

    private fun getRare(value: Int): Int {
        return when (value) {
            in 1..2 -> R.drawable.elixir_common
            in 3..4 -> R.drawable.elixir_rare
            in 5..6 -> R.drawable.elixir_epic
            in 7..10 -> R.drawable.elixir_legendary
            else -> R.drawable.elixir_common
        }
    }

    private fun getRareColor(value: Int): Int {
        return when (value) {
            in 1..2 -> Color.GRAY
            in 3..4 -> Color.YELLOW
            in 5..6 -> Color.MAGENTA
            in 7..10 -> Color.CYAN
            else -> Color.GRAY
        }
    }

    companion object {
        private const val START_DELAY = 1200L
        private const val TIME_TO_END_ANIMATION = 3200L
        private const val DEF_TYPE = "drawable"
        private const val ICON_PREFIX: String = "icon"
        private const val ICONS_COUNT: Int = 82
        private const val CARDS_COUNT: Int = 7
        private const val MIN_LVL_RARE: Int = 1
        private const val MAX_LVL_RARE: Int = 11
    }
}