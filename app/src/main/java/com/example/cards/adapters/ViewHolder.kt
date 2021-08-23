package com.example.cards.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KFunction0

abstract class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(data: T) {}
}