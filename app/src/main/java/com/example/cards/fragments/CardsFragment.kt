package com.example.cards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.cards.R
import com.example.cards.adapters.RecyclerViewAdapter
import com.example.cards.adapters.SimpleItemTouchHelperCallback
import com.example.cards.databinding.CardsFragmentBinding
import com.example.cards.factories.CardViewHolderFactory
import com.example.cards.factories.ViewModelFactory
import com.example.cards.models.Average
import com.example.cards.models.Card
import com.example.cards.viewmodels.CardViewModel

private const val SPAN_COUNT = 4

class CardsFragment : Fragment() {
    private lateinit var viewModel: CardViewModel
    private lateinit var adapter: RecyclerViewAdapter<Card>
    private var _binding: CardsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelFactory()).get(CardViewModel::class.java)
        _binding = CardsFragmentBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardList.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        val clearAnimation: Animation =
            AnimationUtils.loadAnimation(activity, R.anim.clear_deck_animation)

        binding.randomButton.setOnClickListener {
            onRandomButtonClick(clearAnimation)
        }

        adapter = RecyclerViewAdapter(
            CardViewHolderFactory(),
            R.layout.card_item,
            this::onCardClick
        )

        val touchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(adapter))
        touchHelper.attachToRecyclerView(binding.cardList)

        binding.cardList.adapter = adapter

        viewModel.data.observe(viewLifecycleOwner) { data ->
            adapter.addItems(data)
        }

        viewModel.loadedComplete.observe(viewLifecycleOwner) {
            binding.costLine.visibility = View.INVISIBLE
        }

        viewModel.averageCost.observe(viewLifecycleOwner) { average ->
            setAverage(average)
        }

        viewModel.readyToNewData.observe(viewLifecycleOwner) { ready ->
            binding.randomButton.isEnabled = ready
        }

        viewModel.newCard.observe(viewLifecycleOwner) { card ->
            adapter.replaceItem(card, card.position)
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
        binding.totalElixir.setImageResource(average.elixir)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = binding.root

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}