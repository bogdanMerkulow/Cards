package com.example.cards.adapters

import android.view.View
import android.view.animation.Animation
import androidx.recyclerview.widget.RecyclerView

abstract class ViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    open fun bind(data: T, position: Int) {}
}