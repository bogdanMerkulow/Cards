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
private const val CARD_BACKGROUND_SIZE = -1f
private const val MINIMUM_SIZE = 0f
private const val DEFAULT_POS_AND_SIZE = 1f
private const val ANIMATION_DURATION = 1000L
private const val BEYOND_SCREEN = 1200f
private const val START_POS_X = -210f
private const val START_POS_Y = -225f

private fun createView(parent: ViewGroup): View = LayoutInflater.from(parent.context)
    .inflate(R.layout.card_item, parent, false)

class CardViewHolder(parent: ViewGroup, private val listener: (Int) -> Unit) :
    ViewHolder<Card>(createView(parent)) {

    private val binding: CardItemBinding = CardItemBinding.bind(itemView)

    override fun bind(data: Card) {
        val endPodDivider = (adapterPosition + 1) * END_POS_DIVIDER
        val positionFrom = getPositionByAdapterPosition(adapterPosition)

        val setCardOnStartPosAnimation = AnimationHelper.getTranslateAnimation(
            positionFrom.x, positionFrom.x,
            positionFrom.y, positionFrom.y,
            ANIMATION_DURATION
        )

        val placeCardToEndPos = AnimationHelper.getTranslateAnimation(
            positionFrom.x, DEFAULT_POS_AND_SIZE,
            positionFrom.y, DEFAULT_POS_AND_SIZE,
            ((adapterPosition * ANIMATION_DURATION) / endPodDivider).toLong()
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
                binding.cardElixir.visibility = View.VISIBLE
                binding.cardLvl.visibility = View.VISIBLE
                binding.cardElixir.startAnimation(elixirFadeAnimation)
                binding.cardLvl.startAnimation(elixirFadeAnimation)
            }
        )

        flipCardToBackAnimation.setListener(
            onStart = {
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(ANIMATION_DURATION / IMAGE_FADE_MULTIPLIER)
                        withContext(Dispatchers.Main) {
                            binding.cardLvl.text = null
                            binding.cardElixir.setImageResource(0)
                            binding.cardImage.setImageResource(R.drawable.card_back)
                        }
                    }
                }
            },
            onEnd = {
                itemView.startAnimation(dropCardAnimation)
            }
        )

        itemView.apply {
            binding.cardLvl.text = data.lvl.toString()
            binding.cardElixir.setImageResource(data.rare)
            binding.cardElixir.visibility = View.INVISIBLE
            binding.cardLvl.visibility = View.INVISIBLE
            binding.cardImage.setImageResource(R.drawable.card_back)

            setOnClickListener {
                listener(adapterPosition)
                itemView.startAnimation(flipCardToBackAnimation)
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