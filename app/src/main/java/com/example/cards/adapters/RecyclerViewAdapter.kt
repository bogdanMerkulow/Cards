package com.example.cards.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cards.extections.swap

class RecyclerViewAdapter<T>(
    private val viewHolderFactory: ViewHolderFactory<T>,
    val holderList: MutableList<ViewHolder<T>> = mutableListOf()
) : RecyclerView.Adapter<ViewHolder<T>>(), ItemTouchHelperAdapter {

    private val items: MutableList<T> = mutableListOf()

    fun addItems(items: List<T>) {
        holderList.clear()
        this.items.clear()
        this.items.addAll(items as MutableList<T>)
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
        holderList.add(holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        items.swap(fromPosition, toPosition)

        notifyItemMoved(fromPosition, toPosition)
        return true
    }
}