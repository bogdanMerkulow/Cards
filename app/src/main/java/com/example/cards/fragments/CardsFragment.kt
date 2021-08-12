package com.example.cards.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cards.R
import com.example.cards.adapters.RecyclerViewAdapter
import com.example.cards.databinding.CardsFragmentBinding
import com.example.cards.factories.CardViewHolderFactory
import com.example.cards.models.Card
import com.example.cards.viewmodels.CardViewModel

class CardsFragment : Fragment() {
    private lateinit var viewModel: CardViewModel
    private lateinit var adapter: RecyclerViewAdapter<Card>
    private var _binding: CardsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = CardViewModel(requireActivity().application)
        _binding = CardsFragmentBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardList: RecyclerView = binding.rwCardList
        val randomButton: Button = binding.randomButton
        val averageCost: TextView = binding.averageCost
        val costLine = binding.costLine
        cardList.layoutManager = GridLayoutManager(context, 4)

        randomButton.setOnClickListener {
            viewModel.getNewShuffledData()
            randomButton.isEnabled = false
            costLine.isVisible = false
        }

        adapter = RecyclerViewAdapter(
            CardViewHolderFactory(),
            R.layout.card_item,
            {Log.e("test123", "click")},
            {Log.e("test123", "long click")}
        )

        cardList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.addItems(data)
        }

        viewModel.loadedComplete.observe(viewLifecycleOwner) { isComplete ->
            randomButton.isEnabled = isComplete
            costLine.isVisible = isComplete
        }

        viewModel.averageCost.observe(viewLifecycleOwner) { cost ->
            averageCost.text = cost.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root
}