package com.example.cards.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.cards.R
import com.example.cards.adapters.RecyclerViewAdapter
import com.example.cards.adapters.SimpleItemTouchHelperCallback
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
    private var firstStart: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = CardViewModel(requireActivity().application)
        _binding = CardsFragmentBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvCardList.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        val clearAnimation: Animation =
            AnimationUtils.loadAnimation(activity, R.anim.clear_deck_animation)

        binding.randomButton.setOnClickListener {
            onRandomButtonClick(clearAnimation)
        }

        adapter = RecyclerViewAdapter(
            CardViewHolderFactory(),
            R.layout.card_item,
        )

        val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.rvCardList)


        binding.rvCardList.adapter = adapter

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

    private fun onRandomButtonClick(clearAnimation: Animation) {
        viewModel.getNewShuffledData()
        binding.randomButton.isEnabled = false
        if (!firstStart)
            binding.rvCardList.startAnimation(clearAnimation)

        firstStart = false
    }

    private fun setLoader(isComplete: Boolean) {
        binding.randomButton.isEnabled = isComplete
        binding.deck.isVisible = isComplete

        binding.costLine.visibility = View.INVISIBLE
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

    companion object {
        private const val SPAN_COUNT = 4
    }
}