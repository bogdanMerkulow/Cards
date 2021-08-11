package com.example.cards.factories

import android.view.View
import com.example.cards.adapters.ViewHolder
import com.example.cards.adapters.ViewHolderFactory
import com.example.cards.viewholders.CardViewHolder

class CardViewHolderFactory<T> : ViewHolderFactory<T> {
    override fun create(view: View): ViewHolder<T> {
        return CardViewHolder(view) as ViewHolder<T>
    }
}