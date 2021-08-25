package com.example.cards.cards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.cards.adapters.RecyclerViewAdapter
import com.example.cards.adapters.SimpleItemTouchHelperCallback
import com.example.cards.cards.factories.CardViewHolderFactory
import com.example.cards.cards.viewholders.CardViewHolder
import com.example.cards.cards.viewmodels.CardViewModel
import com.example.cards.databinding.CardsFragmentBinding
import com.example.cards.factories.ViewModelFactory
import com.example.cards.models.Average
import com.example.cards.models.Card
import kotlinx.coroutines.*

private const val SPAN_COUNT = 4
private const val FULL_ALPHA = 1f
private const val LOW_ALPHA = 0.8f

class CardsFragment : Fragment() {
    private val viewModel: CardViewModel by viewModels(factoryProducer = { ViewModelFactory() })
    private lateinit var adapter: RecyclerViewAdapter<Card>
    private var _binding: CardsFragmentBinding? = null
    private var touchHelper: ItemTouchHelper? = null
    private val binding get() = _binding!!
    private var ready = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CardsFragmentBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            cardList.layoutManager = GridLayoutManager(context, SPAN_COUNT)
            randomButton.setOnClickListener {
                onRandomButtonClick()
            }
        }

        adapter = RecyclerViewAdapter(
            CardViewHolderFactory(this::onCardClick),
        )

        binding.cardList.adapter = adapter

        touchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(adapter))
        touchHelper?.attachToRecyclerView(binding.cardList)

        with(viewModel) {
            data.observe(viewLifecycleOwner, adapter::addItems)

            averageCost.observe(viewLifecycleOwner, ::setAverage)

            loadedComplete.observe(viewLifecycleOwner) { visibility ->
                binding.costLine.visibility = visibility
            }

            readyToNewData.observe(viewLifecycleOwner, ::onChangeReady)

            newCard.observe(viewLifecycleOwner) { newCard ->
                adapter.replaceItem(newCard.card, newCard.position)
            }
        }
    }

    private fun onRandomButtonClick() {
        viewModel.getNewShuffledData()
        (adapter.holderList as List<CardViewHolder>).forEach { card ->
            card.startAnimation()
        }
    }

    private fun onCardClick(position: Int) {
        if (ready) {
            (adapter.holderList as List<CardViewHolder>)[position].newCardAnimation()
            viewModel.getRandomUniqueCard(position)
        }
    }

    private fun setAverage(average: Average) {
        with(binding) {
            averageCost.text = average.cost.toString()
            averageCost.setTextColor(average.rareColor)
            averageCostTitle.setTextColor(average.rareColor)
            averageElixir.setImageResource(average.elixir)
        }
    }

    private fun onChangeReady(ready: Boolean) {
        this@CardsFragment.ready = ready

        if (ready) {
            touchHelper?.attachToRecyclerView(binding.cardList)
            (adapter.holderList as List<CardViewHolder>).map { it.itemView.alpha = FULL_ALPHA }
        } else {
            touchHelper?.attachToRecyclerView(null)
            (adapter.holderList as List<CardViewHolder>).map { it.itemView.alpha = LOW_ALPHA }
        }

        binding.randomButton.isEnabled = ready
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}