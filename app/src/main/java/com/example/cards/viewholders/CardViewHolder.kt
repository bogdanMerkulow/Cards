package com.example.cards.viewholders

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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

private const val MAGIC_MULTIPLIER = 1.2f
private const val IMAGE_FADE_MULTIPLIER = 2
private const val ELIXIR_FADE_MULTIPLIER = 3
private const val SECOND_ITEM_POS_MULTIPLIER = 2f
private const val THIRD_ITEM_POS_MULTIPLIER = 3f
private const val PIVOT_SIZE = 0.5f
private const val CARD_BACKGROUND_SIZE = -1f
private const val DRAG_SIZE = 1.1f
private const val MINIMUM_SIZE = 0f
private const val DEFAULT_POS_AND_SIZE = 1f
private const val DRAG_DURATION = 100L
private const val ANIMATION_DURATION = 1000L
private const val BEYOND_SCREEN = 1200f
private const val START_POS_X = -210f
private const val START_POS_Y = -225f

private fun getView(parent: ViewGroup): View = LayoutInflater.from(parent.context)
    .inflate(R.layout.card_item, parent, false)

class CardViewHolder(parent: ViewGroup, private val listener: (Int) -> Unit) :
    ViewHolder<Card>(getView(parent)) {
    private val elixir: ImageView = itemView.findViewById(R.id.card_elixir)
    private val image: ImageView = itemView.findViewById(R.id.card_image)
    private val lvl: TextView = itemView.findViewById(R.id.card_lvl)

    override fun bind(data: Card) {
        val multiplier = (adapterPosition + 1) * MAGIC_MULTIPLIER
        val positionFrom = when (adapterPosition) {
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
            duration = ((adapterPosition * ANIMATION_DURATION) / multiplier).toLong()
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

        val dragScaleAnimation = ScaleAnimation(
            DEFAULT_POS_AND_SIZE, DRAG_SIZE,
            DEFAULT_POS_AND_SIZE, DRAG_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE
        ).apply {
            duration = ANIMATION_DURATION / ELIXIR_FADE_MULTIPLIER
            fillAfter = true
        }

        val cancelDragScaleAnimation = ScaleAnimation(
            DRAG_SIZE, DEFAULT_POS_AND_SIZE,
            DRAG_SIZE, DEFAULT_POS_AND_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_SIZE
        ).apply {
            duration = DRAG_DURATION
            fillAfter = true
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
                listener(adapterPosition)
                itemView.startAnimation(flipCardToBackAnimation)
            }

            setOnLongClickListener { false }

            setOnTouchListener { _, event ->
                when (event!!.action) {
                    MotionEvent.ACTION_DOWN -> itemView.startAnimation(dragScaleAnimation)
                    MotionEvent.ACTION_CANCEL -> itemView.startAnimation(cancelDragScaleAnimation)
                    MotionEvent.ACTION_UP -> itemView.startAnimation(cancelDragScaleAnimation)
                }

                true
            }

            startAnimation(setCardOnStartPosAnimation)
        }
    }
}