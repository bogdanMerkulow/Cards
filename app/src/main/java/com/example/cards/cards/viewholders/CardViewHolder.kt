package com.example.cards.cards.viewholders

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.cards.R
import com.example.cards.adapters.ViewHolder
import com.example.cards.databinding.CardItemBinding
import com.example.cards.extections.setListener
import com.example.cards.models.Card
import com.example.cards.models.Position
import kotlinx.coroutines.*

private const val END_POS_DIVIDER = 1.2f
private const val IMAGE_FADE_MULTIPLIER = 2
private const val ELIXIR_FADE_MULTIPLIER = 3
private const val SECOND_ITEM_POS_MULTIPLIER = 2f
private const val THIRD_ITEM_POS_MULTIPLIER = 3f
private const val DROP_MULTIPLIER = 120L
private const val CARD_BACKGROUND_SIZE = -1f
private const val MINIMUM_SIZE = 0f
private const val DEFAULT_POS_AND_SIZE = 1f
private const val ANIMATION_DURATION = 1000L
private const val BEYOND_SCREEN = 1400f
private const val DROP_ANIMATION = 1800L
private const val START_POS_X = -210f
private const val START_POS_Y = -225f

private fun createView(parent: ViewGroup): View = LayoutInflater.from(parent.context)
    .inflate(R.layout.card_item, parent, false)

class CardViewHolder(parent: ViewGroup, private val listener: (Int) -> Unit) :
    ViewHolder<Card>(createView(parent)) {
    var startAnimation: () -> Unit = {}
    var newCardAnimation: () -> Unit = {}
    private var endPodDivider = 0f
    private var dropAnimationTime = 0L
    private var placeCardToEndPosAnimationTime = 0L
    private val binding: CardItemBinding = CardItemBinding.bind(itemView)

    override fun bind(data: Card) {
        val positionFrom = getPositionByAdapterPosition(adapterPosition)
        endPodDivider = (adapterPosition + 1) * END_POS_DIVIDER
        dropAnimationTime = DROP_ANIMATION + (adapterPosition * DROP_MULTIPLIER)
        placeCardToEndPosAnimationTime = ((adapterPosition * ANIMATION_DURATION) / endPodDivider).toLong()

        val dropAnimation = AnimationHelper.getTranslateAnimation(
            DEFAULT_POS_AND_SIZE, DEFAULT_POS_AND_SIZE,
            DEFAULT_POS_AND_SIZE, BEYOND_SCREEN,
            dropAnimationTime
        )

        startAnimation = { itemView.startAnimation(dropAnimation) }

        val setCardOnStartPosAnimation = AnimationHelper.getTranslateAnimation(
            positionFrom.x, positionFrom.x,
            positionFrom.y, positionFrom.y,
            ANIMATION_DURATION
        )

        val placeCardToEndPos = AnimationHelper.getTranslateAnimation(
            positionFrom.x, DEFAULT_POS_AND_SIZE,
            positionFrom.y, DEFAULT_POS_AND_SIZE,
            placeCardToEndPosAnimationTime
        )

        val flipCardToFrontAnimation = AnimationHelper.getScaleAnimation(
            CARD_BACKGROUND_SIZE, DEFAULT_POS_AND_SIZE,
            DEFAULT_POS_AND_SIZE, DEFAULT_POS_AND_SIZE,
            ANIMATION_DURATION
        )

        val flipCardToBackAnimation = AnimationHelper.getScaleAnimation(
            DEFAULT_POS_AND_SIZE, CARD_BACKGROUND_SIZE,
            DEFAULT_POS_AND_SIZE, DEFAULT_POS_AND_SIZE,
            ANIMATION_DURATION
        )

        val elixirFadeAnimation = AnimationHelper.getScaleAnimation(
            MINIMUM_SIZE, DEFAULT_POS_AND_SIZE,
            MINIMUM_SIZE, DEFAULT_POS_AND_SIZE,
            ANIMATION_DURATION / ELIXIR_FADE_MULTIPLIER
        )

        val dropCardAnimation = AnimationHelper.getTranslateAnimation(
            DEFAULT_POS_AND_SIZE, DEFAULT_POS_AND_SIZE,
            DEFAULT_POS_AND_SIZE, BEYOND_SCREEN,
            ANIMATION_DURATION * 2
        )

        setCardOnStartPosAnimation.setListener(
            onEnd = {
                itemView.startAnimation(placeCardToEndPos)
            }
        )

        placeCardToEndPos.setListener(
            onEnd = {
                itemView.startAnimation(flipCardToFrontAnimation)
            }
        )

        flipCardToFrontAnimation.setListener(
            onStart = {
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(ANIMATION_DURATION / IMAGE_FADE_MULTIPLIER)
                        withContext(Dispatchers.Main) {
                            binding.cardImage.setImageResource(data.image)
                        }
                    }
                }
            },
            onEnd = {
                with(binding) {
                    cardElixir.visibility = View.VISIBLE
                    cardLvl.visibility = View.VISIBLE
                    cardElixir.startAnimation(elixirFadeAnimation)
                    cardLvl.startAnimation(elixirFadeAnimation)
                }
                itemView.clearAnimation()
            }
        )

        flipCardToBackAnimation.setListener(
            onStart = {
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(ANIMATION_DURATION / IMAGE_FADE_MULTIPLIER)
                        withContext(Dispatchers.Main) {
                            with(binding) {
                                cardLvl.text = null
                                cardElixir.setImageResource(0)
                                cardImage.setImageResource(R.drawable.card_back)
                            }
                        }
                    }
                }
            },
            onEnd = {
                itemView.startAnimation(dropCardAnimation)
            }
        )

        with(binding) {
            cardLvl.text = data.lvl.toString()
            cardElixir.setImageResource(data.rare)
            cardElixir.visibility = View.INVISIBLE
            cardLvl.visibility = View.INVISIBLE
            cardImage.setImageResource(R.drawable.card_back)
        }

        with(itemView) {
            newCardAnimation = { startAnimation(flipCardToBackAnimation) }
            
            setOnClickListener {
                listener(adapterPosition)
            }

            setOnLongClickListener { false }

            startAnimation(setCardOnStartPosAnimation)
        }
    }

    private fun getPositionByAdapterPosition(adapterPosition: Int): Position {
        return when (adapterPosition) {
            0 -> Position(DEFAULT_POS_AND_SIZE - adapterPosition,DEFAULT_POS_AND_SIZE - adapterPosition)
            1 -> Position(START_POS_X - adapterPosition, DEFAULT_POS_AND_SIZE - adapterPosition)
            2 -> Position(START_POS_X * SECOND_ITEM_POS_MULTIPLIER - adapterPosition,DEFAULT_POS_AND_SIZE - adapterPosition)
            3 -> Position(START_POS_X * THIRD_ITEM_POS_MULTIPLIER - adapterPosition,DEFAULT_POS_AND_SIZE - adapterPosition)
            4 -> Position(DEFAULT_POS_AND_SIZE - adapterPosition, START_POS_Y - adapterPosition)
            5 -> Position(START_POS_X - adapterPosition, START_POS_Y - adapterPosition)
            6 -> Position(START_POS_X * SECOND_ITEM_POS_MULTIPLIER - adapterPosition,START_POS_Y - adapterPosition)
            7 -> Position(START_POS_X * THIRD_ITEM_POS_MULTIPLIER - adapterPosition,START_POS_Y - adapterPosition)
            else -> throw IndexOutOfBoundsException("card position: $adapterPosition not found")
        }
    }
}