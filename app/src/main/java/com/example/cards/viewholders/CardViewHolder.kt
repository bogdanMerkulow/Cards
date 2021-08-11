package com.example.cards.viewholders

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.cards.R
import com.example.cards.adapters.ViewHolder
import com.example.cards.models.Card

class CardViewHolder(itemView: View) : ViewHolder<Card>(itemView) {
    private val image: ImageView = itemView.findViewById(R.id.card_image)
    private val lvl: TextView = itemView.findViewById(R.id.card_lvl)

    override fun bind(data: Card, listener: (Card) -> Unit) {
        itemView.apply {
            lvl.text = data.lvl.toString()
            setOnClickListener {
                listener(data)
            }

            image.setImageResource(R.drawable.item13)

            /*Glide
                .with(itemView)
                .load(data.image)
                .into(image)
*/
        }
    }
}