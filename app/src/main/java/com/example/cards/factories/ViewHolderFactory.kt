package com.example.cards.factories

import android.view.ViewGroup
import com.example.cards.adapters.ViewHolder

interface ViewHolderFactory<T> {
    fun create(parent: ViewGroup): ViewHolder<T>
}