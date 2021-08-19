package com.example.cards.cards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.cards.R
import com.example.cards.adapters.RecyclerViewAdapter
import com.example.cards.adapters.SimpleItemTouchHelperCallback
import com.example.cards.cards.factories.CardViewHolderFactory
import com.example.cards.cards.viewmodels.CardViewModel
import com.example.cards.databinding.CardsFragmentBinding
import com.example.cards.factories.ViewModelFactory
import com.example.cards.models.Average
import com.example.cards.models.Card

private const val SPAN_COUNT = 4

class CardsFragment : Fragment() {
    private val viewModel: CardViewModel by viewModels(factoryProducer = { ViewModelFactory() })
    private lateinit var adapter: RecyclerViewAdapter<Card>
    private var _binding: CardsFragmentBinding? = null
    private val binding get() = _binding!!

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
        binding.cardList.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        val clearAnimation: Animation =
            AnimationUtils.loadAnimation(activity, R.anim.clear_deck_animation)

        binding.randomButton.setOnClickListener {
            onRandomButtonClick(clearAnimation)
        }

        adapter = RecyclerViewAdapter(
            CardViewHolderFactory(this::onCardClick),
        )

        val touchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(adapter))
        touchHelper.attachToRecyclerView(binding.cardList)

        binding.cardList.adapter = adapter

        with(viewModel) {
            data.observe(viewLifecycleOwner, adapter::addItems)

            averageCost.observe(viewLifecycleOwner, ::setAverage)

            loadedComplete.observe(viewLifecycleOwner) {
                binding.costLine.visibility = View.INVISIBLE
            }

            readyToNewData.observe(viewLifecycleOwner) { ready ->
                binding.randomButton.isEnabled = ready
            }

            newCard.observe(viewLifecycleOwner) { card ->
                adapter.replaceItem(card, card.position)
            }
        }
    }

    private fun onRandomButtonClick(clearAnimation: Animation) {
        viewModel.getNewShuffledData()
        binding.cardList.startAnimation(clearAnimation)
    }

    private fun onCardClick(position: Int) {
        viewModel.getRandomUniqueCard(position)
    }

    private fun setAverage(average: Average) {
        binding.costLine.visibility = View.VISIBLE

        binding.averageCost.text = average.cost.toString()
        binding.averageCost.setTextColor(average.rareColor)
        binding.averageCostTitle.setTextColor(average.rareColor)
        binding.averageElixir.setImageResource(average.elixir)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}