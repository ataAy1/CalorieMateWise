package com.app.home.presentation

import FoodsByDateAdapter
import TodayFoodsAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.home.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val todayFoodsAdapter = TodayFoodsAdapter(emptyList())
        val foodsByDateAdapter = FoodsByDateAdapter(emptyMap(), requireActivity())

        binding.recyclerViewFoodsByDate.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewTodayFoods.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerViewTodayFoods.adapter = todayFoodsAdapter
        binding.recyclerViewFoodsByDate.adapter = foodsByDateAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todayFoods.collect { foods ->
                Log.d("UIUpdate", "Updating UI with today foods: $foods")
                todayFoodsAdapter.updateData(foods)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allFoods.collect { foods ->
                Log.d("UIUpdate", "Updating UI with all foods: $foods")
                foodsByDateAdapter.updateData(foods)
            }
        }

        viewModel.getTodayFoods()
        viewModel.getAllFoods()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
