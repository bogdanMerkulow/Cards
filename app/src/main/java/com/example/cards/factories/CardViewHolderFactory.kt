package com.example.cards.factories

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.cards.adapters.ViewHolder
import com.example.cards.viewholders.CardViewHolder

class CardViewHolderFactory<T>() : ViewHolderFactory<T> {
    override fun create(parent: ViewGroup): ViewHolder<T> {
        return CardViewHolder(parent) as ViewHolder<T>
    }
}