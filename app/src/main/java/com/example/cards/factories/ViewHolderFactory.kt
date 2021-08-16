package com.example.cards.factories

import android.view.View
import com.example.cards.adapters.ViewHolder

interface ViewHolderFactory<T> {
    fun create(view: View): ViewHolder<T>
}