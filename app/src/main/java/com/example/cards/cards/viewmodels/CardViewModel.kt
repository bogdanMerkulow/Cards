package com.example.cards.cards.viewmodels

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.*
import com.example.cards.R
import com.example.cards.models.Average
import com.example.cards.models.Card
import com.example.cards.models.GameCard
import com.example.cards.models.NewCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.random.Random

private const val START_DELAY = 1200L
private const val TIME_TO_DROP_CARD = 2400L
private const val TIME_TO_END_ANIMATION = 3200L
private const val DEF_TYPE = "drawable"
private const val ICON_PREFIX: String = "icon"
private const val ICONS_COUNT: Int = 82
private const val CARDS_COUNT: Int = 8
private const val MIN_LVL_RARE: Int = 1
private const val MAX_LVL_RARE: Int = 11

class CardViewModel(private val context: Context) : ViewModel() {
    private val _data: MutableLiveData<List<GameCard>> = MutableLiveData<List<GameCard>>()
    private val _loadedComplete: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _averageCost: MutableLiveData<Average> = MutableLiveData<Average>()
    private val _readyToNewData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _newCard: MutableLiveData<NewCard> = MutableLiveData<NewCard>()
    private val currentCardsSet: MutableList<Card> = mutableListOf()

    val data: LiveData<List<GameCard>>
        get() = _data

    val loadedComplete: LiveData<Boolean>
        get() = _loadedComplete

    val averageCost: LiveData<Average>
        get() = _averageCost

    val readyToNewData: LiveData<Boolean>
        get() = _readyToNewData

    val newCard: LiveData<NewCard>
        get() = _newCard

    fun getNewShuffledData() {
        viewModelScope.launch(Dispatchers.IO) {
            val cards = mutableListOf<GameCard>()
            var iconsList = mutableListOf<Int>()

            currentCardsSet.clear()
            _readyToNewData.postValue(false)

            getRandomNumbersList(CARDS_COUNT, ICONS_COUNT).forEach { randomNumber ->
                val iconId = context.resources.getIdentifier(
                    "$ICON_PREFIX$randomNumber",
                    DEF_TYPE,
                    context.packageName
                )

                iconsList.add(iconId)
            }

            iconsList.forEach { icon ->
                val cardLvl = Random.nextInt(MIN_LVL_RARE, MAX_LVL_RARE)

                val elixir = getRare(cardLvl)

                cards.add(GameCard(elixir, cardLvl, icon))
            }

            val average = getAverage(cards)

            Thread.sleep(START_DELAY)

            currentCardsSet.addAll(cards)
            _data.postValue(cards)
            _loadedComplete.postValue(true)

            viewModelScope.launch(Dispatchers.IO) {
                Thread.sleep(TIME_TO_END_ANIMATION)
                _averageCost.postValue(average)
                _readyToNewData.postValue(true)
            }
        }
    }

    private fun getRandomNumbersList(cardsCount: Int, iconsCount: Int): List<Int> {
        val numberList = mutableSetOf<Int>()

        for (i in (iconsCount - (cardsCount - 1))..iconsCount) {
            val number = Random.nextInt(1, i)

            if (numberList.contains(number))
                numberList.add(i)
            else
                numberList.add(number)
        }

        return numberList.shuffled()
    }

    fun getRandomUniqueCard(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadedComplete.postValue(false)
            _readyToNewData.postValue(false)

            val randomIcon = getRandomNumbersList(1, ICONS_COUNT)[0]

            val iconId = context.resources.getIdentifier(
                "$ICON_PREFIX$randomIcon",
                DEF_TYPE,
                context.packageName
            )

            if (currentCardsSet.map { it.image == iconId }.contains(true)) {
                getRandomUniqueCard(position)
                return@launch
            }

            val cardLvl = Random.nextInt(MIN_LVL_RARE, MAX_LVL_RARE)
            val elixir = getRare(cardLvl)
            val newCard = NewCard(elixir, cardLvl, iconId, position)

            currentCardsSet.removeAt(position)
            currentCardsSet.add(position, newCard)

            Thread.sleep(TIME_TO_DROP_CARD)

            val average = getAverage(currentCardsSet)

            viewModelScope.launch(Dispatchers.IO) {
                Thread.sleep(TIME_TO_DROP_CARD)
                _averageCost.postValue(average)
                _readyToNewData.postValue(true)
            }

            _newCard.postValue(newCard)
        }
    }

    private fun getAverage(cards: List<Card>): Average {
        val average = Average(0, 0, 0)
        average.cost = cards.map { it.lvl }.sum()
        average.cost = ceil((average.cost.toDouble() / CARDS_COUNT + 1)).toInt()
        average.elixir = getRare(average.cost)
        average.rareColor = getRareColor(average.cost)

        return average
    }

    private fun getRare(value: Int): Int {
        return when (value) {
            in 1..2 -> R.drawable.elixir_common
            in 3..4 -> R.drawable.elixir_rare
            in 5..6 -> R.drawable.elixir_epic
            in 7..10 -> R.drawable.elixir_legendary
            else -> throw IndexOutOfBoundsException("elixir id: $value not found")
        }
    }

    private fun getRareColor(value: Int): Int {
        return when (value) {
            in 1..2 -> Color.GRAY
            in 3..4 -> Color.YELLOW
            in 5..6 -> Color.MAGENTA
            in 7..10 -> Color.CYAN
            else -> throw IndexOutOfBoundsException("color id: $value not found")
        }
    }
}