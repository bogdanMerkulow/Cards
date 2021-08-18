package com.example.cards.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cards.factories.ViewHolderFactory
import java.util.*

class RecyclerViewAdapter<T>(
    private val viewHolderFactory: ViewHolderFactory<T>,
) : RecyclerView.Adapter<ViewHolder<T>>(), ItemTouchHelperAdapter {

    private var items: MutableList<T> = mutableListOf()

    fun addItems(items: List<T>) {
        this.items = items as MutableList<T>
        notifyDataSetChanged()
    }

    fun replaceItem(item: T, pos: Int) {
        items.removeAt(pos)
        items.add(pos, item)

        notifyItemChanged(pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return viewHolderFactory.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val from = fromPosition.coerceAtLeast(toPosition)
        val to = fromPosition.coerceAtMost(toPosition)

        Collections.swap(items, from, to)

        notifyItemMoved(fromPosition, toPosition)
        return true
    }
}