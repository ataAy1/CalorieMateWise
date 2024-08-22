package com.app.profile.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.model.FoodModelParcelize
import com.app.profile.databinding.FragmentFoodHistoryBinding

class FoodHistoryFragment : Fragment() {

    private val args: FoodHistoryFragmentArgs by navArgs()
    private var _binding: FragmentFoodHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener{
            findNavController().navigateUp()
        }

        val foodList: Array<FoodModelParcelize> = args.foodList
        setupRecyclerView(foodList)
    }

    private fun setupRecyclerView(foodList: Array<FoodModelParcelize>) {
        val adapter = FoodHistoryAdapter(foodList)
        binding.mealsDetailRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.mealsDetailRecyclerview.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
