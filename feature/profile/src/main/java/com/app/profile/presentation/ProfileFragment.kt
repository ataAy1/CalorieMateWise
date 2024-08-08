package com.app.profile.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.model.FoodModelParcelize
import com.app.profile.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        profileAdapter = ProfileAdapter { date, foods ->
            Log.d("ProfileFragment", "Selected date: $date, Foods: $foods")

            val action = ProfileFragmentDirections.actionProfileFragmentToFoodHistoryFragment(
                foods.toTypedArray()
            )
            findNavController().navigate(action)
        }
        binding.foodHistoryRecyclerView.apply {
            adapter = profileAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            profileViewModel.uiState.collect { uiState ->
                val foodParcelizeList = uiState.foodList.map { food ->
                    FoodModelParcelize(
                        food.id, food.label, food.calories, food.protein, food.fat,
                        food.carbohydrates, food.image, food.date, food.year,
                        food.yearOfMonth, food.dayOfMonth, food.dayName
                    )
                }
                profileAdapter.updateData(foodParcelizeList)
                binding.profileProgressBar.visibility = if (uiState.isLoading) View.VISIBLE else View.GONE
                uiState.error?.let {
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
