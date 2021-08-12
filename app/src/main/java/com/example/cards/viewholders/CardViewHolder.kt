package com.example.cards.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.cards.R
import com.example.cards.adapters.ViewHolder
import com.example.cards.models.Card

class CardViewHolder(itemView: View) : ViewHolder<Card>(itemView) {
    private val elixir: ImageView = itemView.findViewById(R.id.card_elixir)
    private val image: ImageView = itemView.findViewById(R.id.card_image)
    private val lvl: TextView = itemView.findViewById(R.id.card_lvl)

    override fun bind(data: Card, listener: (Card) -> Unit, dragAndDropListener: (Unit) -> Unit) {
        itemView.apply {
            lvl.text = data.lvl.toString()
            elixir.setImageResource(data.rare)
            image.setImageResource(data.image)

            setOnClickListener {
                listener(data)
            }
        }
    }
}