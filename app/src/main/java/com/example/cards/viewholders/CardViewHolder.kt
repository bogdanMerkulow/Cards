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

        val multiplier = if (position < 4) 5 else 3

        when(position) {
            0 -> {
                fromY = 1f
                fromX = 1f
            }
            1 -> {
                fromY = 1f
                fromX = DEFAULT_POS_X
            }
            2 -> {
                fromY = 1f
                fromX = DEFAULT_POS_X * 2f
            }
            3 -> {
                fromY = 1f
                fromX = DEFAULT_POS_X * 3f
            }
            4 -> {
                fromY = DEFAULT_POS_Y
                fromX = 1f
            }
            5 -> {
                fromY = DEFAULT_POS_Y
                fromX = DEFAULT_POS_X
            }
            6 -> {
                fromY = DEFAULT_POS_Y
                fromX = DEFAULT_POS_X * 2f
            }
            7 -> {
                fromY = DEFAULT_POS_Y
                fromX = DEFAULT_POS_X * 3f
            }
        }

        val anim1 = TranslateAnimation(
            fromX, fromX,
            fromY, fromY
        ).apply {
            duration = 1000
        }

        val anim2 = TranslateAnimation(
            fromX, 1f,
            fromY, 1f
        ).apply {
            duration = ((9000 - position * 1000) / multiplier).toLong()
        }

        anim1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                itemView.startAnimation(anim2)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })

        val fadeIn = ScaleAnimation(
            -1f,
            1f,
            1f,
            1f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        fadeIn.duration = 1000
        fadeIn.fillAfter = true

        anim2.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                itemView.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        fadeIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                elixir.visibility = View.VISIBLE
                lvl.visibility = View.VISIBLE
                image.setImageResource(data.image)
            }

            override fun onAnimationRepeat(animation: Animation?) {}

        })

        itemView.startAnimation(anim1)
    }

    companion object {
        private const val DEFAULT_POS_X = -210f
        private const val DEFAULT_POS_Y = -230f
    }
}