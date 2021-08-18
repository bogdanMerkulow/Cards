package com.example.cards.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cards.factories.ViewHolderFactory
import java.util.*

class RecyclerViewAdapter<T>(
    private val viewHolderFactory: ViewHolderFactory<T>,
    private val layoutId: Int,
    private val listener: (Int) -> Unit = {}
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

    override fun getItemViewType(viewType: Int): Int = layoutId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)

        return viewHolderFactory.create(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position], listener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            Collections.swap(items, fromPosition, toPosition)
        } else {
            Collections.swap(items, toPosition, fromPosition)
        }

        notifyItemMoved(fromPosition, toPosition)
        return true
    }
}