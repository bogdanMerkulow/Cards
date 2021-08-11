package com.example.cards.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cards.R
import com.example.cards.adapters.RecyclerViewAdapter
import com.example.cards.databinding.CardsFragmentBinding
import com.example.cards.factories.CardViewHolderFactory
import com.example.cards.models.Card


class CardsFragment : Fragment() {
    private lateinit var adapter: RecyclerViewAdapter<Card>
    private var _binding: CardsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CardsFragmentBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardList: RecyclerView = binding.rwCardList
        cardList.layoutManager = GridLayoutManager(context, 4)

        adapter = RecyclerViewAdapter(
            CardViewHolderFactory(),
            R.layout.card_item,
            {Log.e("test123", "click item")}
        )

        cardList.adapter = adapter

        adapter.addItems(
            listOf(
                Card("test", 1, ""),
                Card("test", 2, ""),
                Card("test", 3, ""),
                Card("test", 4, ""),
                Card("test", 5, ""),
                Card("test", 6, ""),
                Card("test", 7, ""),
                Card("test", 8, ""),
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root
}