package com.app.search.presentation

import FoodAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.app.search.R
import com.app.search.data.model.ParsedFood
import com.app.search.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val searchViewModel: SearchViewModel by viewModels()
    private lateinit var adapter: FoodAdapter
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("SearchFragment", "onCreateView called")
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("SearchFragment", "onViewCreated called")

        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        navController = findNavController()
        adapter = FoodAdapter(navController)

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.searchRecylerview.layoutManager = gridLayoutManager
        binding.searchRecylerview.adapter = adapter
    }

    private fun setupSearchView() {
        Log.d("SearchFragment", "Search query submitted:")

        binding.searchFood.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            private var lastQueryTime = 0L
            private val debounceTime = 300L

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    if (it.isNotBlank()) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastQueryTime > debounceTime) {
                            lastQueryTime = currentTime
                            Log.d("SearchFragment", "Search query submitted: $it")
                            searchViewModel.search(it)
                            observeSearchResults()
                        }
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun observeSearchResults() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            searchViewModel.uiState.collect { state ->
                Log.d("SearchFragment", "State received: $state")
                if (state.isLoading) {
                    binding.progressBarSearch.visibility = View.VISIBLE
                    Log.d("SearchFragment", "Loading data...")
                } else {
                    binding.progressBarSearch.visibility = View.GONE
                    Log.d("SearchFragment", "Data loading complete.")

                    state.combinedResponseState?.let { combinedResponse ->
                        if (combinedResponse.isEmpty()) {
                            Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                        } else {
                            val processedFoodList = combinedResponse.map { parsedFood ->
                                val roundedProtein = roundToTwoDecimalPlaces(parsedFood.nutrients.PROCNT)
                                val roundedCalories = roundToTwoDecimalPlaces(parsedFood.nutrients.ENERC_KCAL)
                                val roundedFat = roundToTwoDecimalPlaces(parsedFood.nutrients.FAT)
                                val roundedCarbs = roundToTwoDecimalPlaces(parsedFood.nutrients.CHOCDF)

                                parsedFood.copy(
                                    nutrients = parsedFood.nutrients.copy(
                                        PROCNT = roundedProtein,
                                        ENERC_KCAL = roundedCalories,
                                        FAT = roundedFat,
                                        CHOCDF = roundedCarbs
                                    )
                                )
                            }

                            adapter.submitList(processedFoodList)
                        }
                    }

                    state.error?.let { error ->
                        Log.e("SearchFragment", "Error occurred: $error")
                        Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun roundToTwoDecimalPlaces(value: Double): Double {
        return String.format("%.1f", value).toDouble()
    }
}
