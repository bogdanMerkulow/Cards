package com.example.cards.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cards.extections.swap

class RecyclerViewAdapter<T>(
    private val viewHolderFactory: ViewHolderFactory<T>,
) : RecyclerView.Adapter<ViewHolder<T>>(), ItemTouchHelperAdapter {

    val holderList: MutableList<ViewHolder<T>> = mutableListOf()
    private val items: MutableList<T> = mutableListOf()

    fun addItems(items: List<T>) {
        holderList.clear()
        this.items.clear()
        this.items.addAll(items as MutableList<T>)
        notifyDataSetChanged()
    }

    fun replaceItem(item: T, pos: Int) {
        items.removeAt(pos)
        holderList.removeAt(pos)
        items.add(pos, item)
        notifyItemChanged(pos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return viewHolderFactory.create(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position])
        holderList.add(position, holder)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                updateItems(i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                updateItems(i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    private fun updateItems(from: Int, to: Int) {
        items.swap(from, to)
        holderList.swap(from, to)
    }
}