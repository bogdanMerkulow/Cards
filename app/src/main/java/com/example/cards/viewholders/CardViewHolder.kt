package com.example.cards.viewholders

import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import com.example.cards.R
import com.example.cards.adapters.ViewHolder
import com.example.cards.models.Card
import com.example.cards.models.Position
import kotlinx.coroutines.*
import java.lang.Exception

private const val MAGIC_MULTIPLIER = 1.2f
private const val IMAGE_FADE_MULTIPLIER = 2
private const val ELIXIR_FADE_MULTIPLIER = 3
private const val SECOND_ITEM_POS_MULTIPLIER = 2f
private const val THIRD_ITEM_POS_MULTIPLIER = 3f
private const val PIVOT_SIZE = 0.5f
private const val CARD_BACKGROUND_SIZE = -1f
private const val MINIMUM_SIZE = 0f
private const val DEFAULT_POS_AND_SIZE = 1f
private const val ANIMATION_DURATION = 1000L
private const val BEYOND_SCREEN = 1200f
private const val START_POS_X = -210f
private const val START_POS_Y = -225f

class CardViewHolder(itemView: View) : ViewHolder<Card>(itemView) {
    private val elixir: ImageView = itemView.findViewById(R.id.card_elixir)
    private val image: ImageView = itemView.findViewById(R.id.card_image)
    private val lvl: TextView = itemView.findViewById(R.id.card_lvl)

    override fun bind(data: Card, position: Int, listener: (Int) -> Unit) {

        val multiplier = (position + 1) * MAGIC_MULTIPLIER

        val positionFrom = when (position) {
            0 -> Position(DEFAULT_POS_AND_SIZE - position, DEFAULT_POS_AND_SIZE - position)
            1 -> Position(START_POS_X - position, DEFAULT_POS_AND_SIZE - position)
            2 -> Position(START_POS_X * SECOND_ITEM_POS_MULTIPLIER - position, DEFAULT_POS_AND_SIZE - position)
            3 -> Position(START_POS_X * THIRD_ITEM_POS_MULTIPLIER - position, DEFAULT_POS_AND_SIZE - position)
            4 -> Position(DEFAULT_POS_AND_SIZE - position, START_POS_Y - position)
            5 -> Position(START_POS_X - position, START_POS_Y - position)
            6 -> Position(START_POS_X * SECOND_ITEM_POS_MULTIPLIER - position, START_POS_Y - position)
            7 -> Position(START_POS_X * THIRD_ITEM_POS_MULTIPLIER - position, START_POS_Y - position)
            else -> throw Exception()
        }

        val setCardOnStartPosAnimation = TranslateAnimation(
            positionFrom.x, positionFrom.x,
            positionFrom.y, positionFrom.y
        ).apply {
            duration = ANIMATION_DURATION
        }

        val placeCardToEndPos = TranslateAnimation(
            positionFrom.x, DEFAULT_POS_AND_SIZE,
            positionFrom.y, DEFAULT_POS_AND_SIZE
        ).apply {
            duration = ((position * ANIMATION_DURATION) / multiplier).toLong()
        }

        setCardOnStartPosAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                itemView.startAnimation(placeCardToEndPos)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        val flipCardToFrontAnimation = ScaleAnimation(
            CARD_BACKGROUND_SIZE, DEFAULT_POS_AND_SIZE,
            DEFAULT_POS_AND_SIZE, DEFAULT_POS_AND_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE
        ).apply {
            duration = ANIMATION_DURATION
            fillAfter = true
        }

        val flipCardToBackAnimation = ScaleAnimation(
            DEFAULT_POS_AND_SIZE, CARD_BACKGROUND_SIZE,
            DEFAULT_POS_AND_SIZE, DEFAULT_POS_AND_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE
        ).apply {
            duration = ANIMATION_DURATION
            fillAfter = true
        }

        val elixirFadeAnimation = ScaleAnimation(
            MINIMUM_SIZE, DEFAULT_POS_AND_SIZE,
            MINIMUM_SIZE, DEFAULT_POS_AND_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE
        ).apply {
            duration = ANIMATION_DURATION / ELIXIR_FADE_MULTIPLIER
            fillAfter = true
        }

        val dropCardAnimation = TranslateAnimation(
            DEFAULT_POS_AND_SIZE, DEFAULT_POS_AND_SIZE,
            DEFAULT_POS_AND_SIZE, BEYOND_SCREEN
        ).apply {
            duration = ANIMATION_DURATION * 2
        }

        placeCardToEndPos.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                itemView.startAnimation(flipCardToFrontAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        flipCardToFrontAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(ANIMATION_DURATION / IMAGE_FADE_MULTIPLIER)
                        withContext(Dispatchers.Main) {
                            image.setImageResource(data.image)
                        }
                    }
                }
            }

            override fun onAnimationEnd(animation: Animation?) {
                elixir.visibility = View.VISIBLE
                lvl.visibility = View.VISIBLE
                elixir.startAnimation(elixirFadeAnimation)
                lvl.startAnimation(elixirFadeAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        flipCardToBackAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(ANIMATION_DURATION / IMAGE_FADE_MULTIPLIER)
                        withContext(Dispatchers.Main) {
                            lvl.text = null
                            elixir.setImageResource(0)
                            image.setImageResource(R.drawable.card_back)
                        }
                    }
                }
            }

            override fun onAnimationEnd(animation: Animation?) {
                itemView.startAnimation(dropCardAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        itemView.apply {
            lvl.text = data.lvl.toString()
            elixir.setImageResource(data.rare)
            elixir.visibility = View.INVISIBLE
            lvl.visibility = View.INVISIBLE
            image.setImageResource(R.drawable.card_back)

            setOnClickListener {
                listener(position)
                itemView.startAnimation(flipCardToBackAnimation)
            }
        }

        itemView.startAnimation(setCardOnStartPosAnimation)
    }
}