package com.example.cards.cards.factories

import android.view.ViewGroup
import com.example.cards.adapters.ViewHolder
import com.example.cards.cards.viewholders.CardViewHolder
import com.example.cards.adapters.ViewHolderFactory
import com.example.cards.models.Card

class CardViewHolderFactory(private val listener: (Int) -> Unit = {}) : ViewHolderFactory<Card> {
    override fun create(parent: ViewGroup): ViewHolder<Card> {
        return CardViewHolder(parent, listener)
    }
}