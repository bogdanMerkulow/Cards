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
import kotlinx.coroutines.*


class CardViewHolder(itemView: View) : ViewHolder<Card>(itemView) {
    private val elixir: ImageView = itemView.findViewById(R.id.card_elixir)
    private val image: ImageView = itemView.findViewById(R.id.card_image)
    private val lvl: TextView = itemView.findViewById(R.id.card_lvl)

    override fun bind(data: Card, listener: (Card) -> Unit, position: Int) {
        itemView.apply {
            lvl.text = data.lvl.toString()
            elixir.setImageResource(data.rare)
            elixir.visibility = View.INVISIBLE
            lvl.visibility = View.INVISIBLE
            image.setImageResource(R.drawable.card_back)

            setOnClickListener {
                listener(data)
            }
        }

        var fromY = 0f
        var fromX = 0f

        val multiplier = (position + 1) * MAGIC_MULTIPLIER

        when (position) {
            0 -> {
                fromY = DEFAULT_POS_AND_SIZE
                fromX = DEFAULT_POS_AND_SIZE
            }
            1 -> {
                fromY = DEFAULT_POS_AND_SIZE
                fromX = START_POS_X
            }
            2 -> {
                fromY = DEFAULT_POS_AND_SIZE
                fromX = START_POS_X * SECOND_ITEM_POS_MULTIPLIER
            }
            3 -> {
                fromY = DEFAULT_POS_AND_SIZE
                fromX = START_POS_X * THIRD_ITEM_POS_MULTIPLIER
            }
            4 -> {
                fromY = START_POS_Y
                fromX = DEFAULT_POS_AND_SIZE
            }
            5 -> {
                fromY = START_POS_Y
                fromX = START_POS_X
            }
            6 -> {
                fromY = START_POS_Y
                fromX = START_POS_X * SECOND_ITEM_POS_MULTIPLIER
            }
            7 -> {
                fromY = START_POS_Y
                fromX = START_POS_X * THIRD_ITEM_POS_MULTIPLIER
            }
        }

        fromX -= position
        fromY -= position

        val setCardOnStartPosAnimation = TranslateAnimation(
            fromX, fromX,
            fromY, fromY
        ).apply {
            duration = ANIMATION_DURATION
        }

        val placeCardToEndPos = TranslateAnimation(
            fromX, DEFAULT_POS_AND_SIZE,
            fromY, DEFAULT_POS_AND_SIZE
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

        val flipCardAnimation = ScaleAnimation(
            CARD_BACKGROUND_SIZE, DEFAULT_POS_AND_SIZE,
            DEFAULT_POS_AND_SIZE, DEFAULT_POS_AND_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE
        )

        flipCardAnimation.duration = ANIMATION_DURATION
        flipCardAnimation.fillAfter = true

        val elixirFadeAnimation = ScaleAnimation(
            MINIMUM_SIZE, DEFAULT_POS_AND_SIZE,
            MINIMUM_SIZE, DEFAULT_POS_AND_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE
        )

        elixirFadeAnimation.duration = ANIMATION_DURATION / ELIXIR_FADE_MULTIPLIER
        elixirFadeAnimation.fillAfter = true

        placeCardToEndPos.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                itemView.startAnimation(flipCardAnimation)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        flipCardAnimation.setAnimationListener(object : Animation.AnimationListener {
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

        itemView.startAnimation(setCardOnStartPosAnimation)
    }

    companion object {
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
        private const val START_POS_X = -210f
        private const val START_POS_Y = -230f
    }
}