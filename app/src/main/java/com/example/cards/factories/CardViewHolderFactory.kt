package com.example.cards.factories

import android.view.ViewGroup
import com.example.cards.adapters.ViewHolder
import com.example.cards.viewholders.CardViewHolder

class CardViewHolderFactory<T>(private val listener: (Int) -> Unit = {}) : ViewHolderFactory<T> {
    override fun create(parent: ViewGroup): ViewHolder<T> {
        return CardViewHolder(parent, listener) as ViewHolder<T>
    }
}