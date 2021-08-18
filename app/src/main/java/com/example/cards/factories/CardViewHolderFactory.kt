package com.example.cards.factories

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.cards.adapters.ViewHolder
import com.example.cards.viewholders.CardViewHolder

class CardViewHolderFactory<T>(private val layoutId: Int) : ViewHolderFactory<T> {
    override fun create(parent: ViewGroup): ViewHolder<T> {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return CardViewHolder(view) as ViewHolder<T>
    }
}