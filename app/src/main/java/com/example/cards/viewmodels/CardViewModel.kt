package com.example.cards.viewmodels

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.cards.R
import com.example.cards.models.Average
import com.example.cards.models.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

class CardViewModel(application: Application) : AndroidViewModel(application) {
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
            val cards = mutableListOf<Card>()
            val ctx = getApplication<Application>().applicationContext
            var iconsList = mutableListOf<Int>()
            val average = Average(0, 0, 0)

            for (i in 0..ICONS_COUNT) {
                val iconId =
                    ctx.resources.getIdentifier("$ICON_PREFIX$i", DEF_TYPE, ctx.packageName)
                iconsList.add(iconId)
            }

            iconsList = iconsList.shuffled() as MutableList<Int>

            for (i in 1..CARDS_COUNT) {
                val cardLvl = Random.nextInt(MIN_LVL_RARE, MAX_LVL_RARE)

                val elixir = getRare(cardLvl)

                cards.add(Card(elixir, cardLvl, iconsList[i]))
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
            }
        }
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
        private const val CARDS_COUNT: Int = 8
        private const val MIN_LVL_RARE: Int = 1
        private const val MAX_LVL_RARE: Int = 10
    }
}