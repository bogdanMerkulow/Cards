package com.example.cards.adapters

import android.view.ViewGroup
import com.example.cards.adapters.ViewHolder
import com.example.cards.models.Card

interface ViewHolderFactory<T> {
    fun create(parent: ViewGroup): ViewHolder<T>
}