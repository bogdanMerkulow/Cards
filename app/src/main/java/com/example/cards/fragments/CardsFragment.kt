package com.example.cards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cards.R
import com.example.cards.adapters.RecyclerViewAdapter
import com.example.cards.databinding.CardsFragmentBinding
import com.example.cards.factories.CardViewHolderFactory
import com.example.cards.models.Average
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
        cardList.layoutManager = GridLayoutManager(context, 4)

        binding.randomButton.setOnClickListener {
            viewModel.getNewShuffledData()
            binding.randomButton.isEnabled = false
            binding.costLine.isVisible = false
        }

        adapter = RecyclerViewAdapter(
            CardViewHolderFactory(),
            R.layout.card_item
        )

        cardList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.addItems(data)
        }

        viewModel.loadedComplete.observe(viewLifecycleOwner) { isComplete ->
            setLoader(isComplete)
        }

        viewModel.averageCost.observe(viewLifecycleOwner) { average ->
            setAverage(average)
        }
    }

    private fun setLoader(isComplete: Boolean) {
        binding.randomButton.isEnabled = isComplete
        binding.costLine.isVisible = isComplete
    }

    private fun setAverage(average: Average) {
        binding.averageCost.text = average.cost.toString()
        binding.totalElixir.setImageResource(average.elixir)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root
}