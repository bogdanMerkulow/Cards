package com.example.cards.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cards.factories.ViewHolderFactory
import java.util.*

class RecyclerViewAdapter<T>(
    private val viewHolderFactory: ViewHolderFactory<T>,
    private val layoutId: Int
) : RecyclerView.Adapter<ViewHolder<T>>(), ItemTouchHelperAdapter {

    private var items: MutableList<T> = mutableListOf()

    fun addItems(items: List<T>) {
        this.items = items as MutableList<T>
        notifyDataSetChanged()
    }

    override fun getItemViewType(viewType: Int): Int = layoutId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        val inflater = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)

        return viewHolderFactory.create(inflater)
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(items, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(items, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        return true
    }
}