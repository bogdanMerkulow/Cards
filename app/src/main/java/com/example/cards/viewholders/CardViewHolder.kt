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

        val multiplier = (position + 1) * 1.2

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
                fromX = START_POS_X * 2f
            }
            3 -> {
                fromY = DEFAULT_POS_AND_SIZE
                fromX = START_POS_X * 3f
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
                fromX = START_POS_X * 2f
            }
            7 -> {
                fromY = START_POS_Y
                fromX = START_POS_X * 3f
            }
        }

        fromX -= position
        fromY -= position

        val setCardOnStartPos = TranslateAnimation(
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

        setCardOnStartPos.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                itemView.startAnimation(placeCardToEndPos)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        val fadeIn = ScaleAnimation(
            -1f, DEFAULT_POS_AND_SIZE,
            1f, DEFAULT_POS_AND_SIZE,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        fadeIn.duration = ANIMATION_DURATION
        fadeIn.fillAfter = true

        val elixirFade = ScaleAnimation(
            0f, DEFAULT_POS_AND_SIZE,
            0f, DEFAULT_POS_AND_SIZE,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        elixirFade.duration = ANIMATION_DURATION / 3
        elixirFade.fillAfter = true

        placeCardToEndPos.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                itemView.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                CoroutineScope(Dispatchers.Main + Job()).launch {
                    withContext(Dispatchers.IO) {
                        Thread.sleep(ANIMATION_DURATION / 2)
                        withContext(Dispatchers.Main) {
                            image.setImageResource(data.image)
                        }
                    }
                }
            }

            override fun onAnimationEnd(animation: Animation?) {
                elixir.visibility = View.VISIBLE
                lvl.visibility = View.VISIBLE
                elixir.startAnimation(elixirFade)
                lvl.startAnimation(elixirFade)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        itemView.startAnimation(setCardOnStartPos)
    }

    companion object {
        private const val DEFAULT_POS_AND_SIZE = 1f
        private const val ANIMATION_DURATION = 1000L
        private const val START_POS_X = -210f
        private const val START_POS_Y = -230f
    }
}