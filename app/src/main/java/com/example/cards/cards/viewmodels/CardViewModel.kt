package com.example.cards.cards.viewmodels

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cards.R
import com.example.cards.models.Average
import com.example.cards.models.Card
import com.example.cards.models.ReplacementCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.round
import kotlin.random.Random

private const val START_DELAY = 1200L
private const val TIME_TO_DROP_CARD = 2400L
private const val TIME_TO_END_ANIMATION = 3200L
private const val DEF_TYPE = "drawable"
private const val ICON_PREFIX: String = "icon"
private const val FIRST_ICON_ID = 1
private const val ICONS_COUNT: Int = 82
private const val CARDS_COUNT: Int = 8
private const val MIN_LVL_RARE: Int = 1
private const val MAX_LVL_RARE: Int = 11
private val COMMON_RARE_RANGE = 1..2
private val RARE_RARE_RANGE = 3..4
private val EPIC_RARE_RANGE = 5..6
private val LEGENDARY_RARE_RANGE = 7..10

class CardViewModel(private val context: Context) : ViewModel() {
    private val _data: MutableLiveData<List<Card>> = MutableLiveData<List<Card>>()
    private val _loadedComplete: MutableLiveData<Int> = MutableLiveData<Int>()
    private val _averageCost: MutableLiveData<Average> = MutableLiveData<Average>()
    private val _readyToNewData: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    private val _replacementCard: MutableLiveData<ReplacementCard> = MutableLiveData<ReplacementCard>()
    private val currentCardsSet: MutableList<Card> = mutableListOf()

    val data: LiveData<List<Card>>
        get() = _data

    val loadedComplete: LiveData<Int>
        get() = _loadedComplete

    val averageCost: LiveData<Average>
        get() = _averageCost

    val readyToNewData: LiveData<Boolean>
        get() = _readyToNewData

    val replacementCard: LiveData<ReplacementCard>
        get() = _replacementCard

    fun getNewShuffledData() {
        viewModelScope.launch(Dispatchers.IO) {
            currentCardsSet.clear()
            _loadedComplete.postValue(View.INVISIBLE)
            _readyToNewData.postValue(false)

            val iconsList = getRandomNumbersList().map {
                context.resources.getIdentifier(
                    "$ICON_PREFIX$it",
                    DEF_TYPE,
                    context.packageName
                )
            }

            val cards = iconsList.map { icon ->
                val cardLvl = Random.nextInt(MIN_LVL_RARE, MAX_LVL_RARE)
                val elixir = getRareIcon(cardLvl)

                Card(elixir, cardLvl, icon)
            }

            val average = getAverage(cards)

            delay(START_DELAY)

            currentCardsSet.addAll(cards)
            _data.postValue(cards)

            delay(TIME_TO_END_ANIMATION)
            _averageCost.postValue(average)
            _readyToNewData.postValue(true)
            _loadedComplete.postValue(View.VISIBLE)
        }
    }

    private fun getRandomNumbersList(): List<Int> {
        val numberList = mutableSetOf<Int>()

        for (i in (ICONS_COUNT - (CARDS_COUNT - 1))..ICONS_COUNT) {
            val number = Random.nextInt(FIRST_ICON_ID, i)

            if (numberList.contains(number))
                numberList.add(i)
            else
                numberList.add(number)
        }

        return numberList.shuffled()
    }

    private tailrec fun randomCard(): Int {
        val randomIcon = Random.nextInt(FIRST_ICON_ID, ICONS_COUNT)

        val iconId = context.resources.getIdentifier(
            "$ICON_PREFIX$randomIcon",
            DEF_TYPE,
            context.packageName
        )

        return if (currentCardsSet.any { it.image == iconId }) randomCard()
        else iconId
    }

    fun getRandomUniqueCard(position: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadedComplete.postValue(View.INVISIBLE)
            _readyToNewData.postValue(false)

            val iconId = randomCard()

            val cardLvl = Random.nextInt(MIN_LVL_RARE, MAX_LVL_RARE)
            val elixir = getRareIcon(cardLvl)
            val newCard = ReplacementCard(Card(elixir, cardLvl, iconId), position)

            with(currentCardsSet) {
                removeAt(position)
                add(position, newCard.card)
            }

            val average = getAverage(currentCardsSet)

            delay(TIME_TO_DROP_CARD)

            _averageCost.postValue(average)
            _replacementCard.postValue(newCard)

            delay(TIME_TO_END_ANIMATION)

            _readyToNewData.postValue(true)
            _loadedComplete.postValue(View.VISIBLE)
        }
    }

    private fun getAverage(cards: List<Card>): Average {
        val cost = round((cards.map { it.lvl }.average())).toInt()
        val rare = getRareIcon(cost)
        val rareColor = getRareColor(cost)

        return Average(cost, rare, rareColor)
    }

    private fun getRareIcon(value: Int): Int {
        return when (value) {
            in COMMON_RARE_RANGE -> R.drawable.elixir_common
            in RARE_RARE_RANGE -> R.drawable.elixir_rare
            in EPIC_RARE_RANGE -> R.drawable.elixir_epic
            in LEGENDARY_RARE_RANGE -> R.drawable.elixir_legendary
            else -> throw IndexOutOfBoundsException("elixir id: $value not found")
        }
    }

    private fun getRareColor(value: Int): Int {
        return when (value) {
            in COMMON_RARE_RANGE -> Color.GRAY
            in RARE_RARE_RANGE -> Color.YELLOW
            in EPIC_RARE_RANGE -> Color.MAGENTA
            in LEGENDARY_RARE_RANGE -> Color.CYAN
            else -> throw IndexOutOfBoundsException("color id: $value not found")
        }
    }
}