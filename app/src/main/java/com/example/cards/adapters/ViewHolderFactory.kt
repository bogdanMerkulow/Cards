package com.example.cards.adapters

import android.view.View

interface ViewHolderFactory<T> {
    fun create(view: View): ViewHolder<T>
}