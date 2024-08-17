package com.app.meal_planning.presentation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.meal_planning.R
import com.app.meal_planning.databinding.FragmentMealPlanningBinding
import com.app.meal_planning.dto.Accept
import com.app.meal_planning.dto.Fit
import com.app.meal_planning.dto.HealthFilter
import com.app.meal_planning.dto.MealPlanRequest
import com.app.meal_planning.dto.NutrientRange
import com.app.meal_planning.dto.Plan
import com.app.meal_planning.dto.SectionDetail
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MealPlanningFragment : Fragment() {

    private val viewModel: MealPlanningViewModel by viewModels()
    private var _binding: FragmentMealPlanningBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMealPlanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        val adapter = MealAdapter()
        binding.recyclerViewMealPlanning.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewMealPlanning.adapter = adapter

        // Define health filters
        val healthFilter = HealthFilter(health = listOf("pork-free"))

        // Define acceptance criteria
        val accept = Accept(all = listOf(healthFilter))

        // Define nutrient range for the whole plan
        val fit = Fit(
            ENERC_KCAL = NutrientRange(min = 1000, max = 2000)
        )

// Define section details for each meal (without kcal constraints)
        val sectionBreakfast = SectionDetail(
            accept = Accept(all = listOf(
                HealthFilter(dish = listOf(
                    "pancake", "main course", "pasta", "biscuits and cookies", "bread",
                    "cereals", "condiments and sauces", "desserts", "drinks", "egg",
                    "ice cream and custard", "pastry", "pies and tarts", "pizza", "preps",
                    "preserve", "salad", "sandwiches", "seafood", "side dish", "soup",
                    "special occasions", "starter", "sweets"
                )),
                HealthFilter(meal = listOf("breakfast"))
            ))
        )

        val sectionLunch = SectionDetail(
            accept = Accept(all = listOf(
                HealthFilter(dish = listOf(
                    "pies and tarts", "main course", "drinks", "biscuits and cookies", "bread",
                    "cereals", "condiments and sauces", "desserts", "egg", "ice cream and custard",
                    "pastry", "pizza", "preps", "preserve", "salad", "sandwiches", "seafood",
                    "side dish", "soup", "special occasions", "starter", "sweets"
                )),
                HealthFilter(meal = listOf("lunch", "dinner"))
            ))
        )

        val sectionDinner = SectionDetail(
            accept = Accept(all = listOf(
                HealthFilter(dish = listOf(
                    "pizza", "sandwiches", "biscuits and cookies", "bread", "cereals",
                    "condiments and sauces", "desserts", "drinks", "egg", "ice cream and custard",
                    "main course", "pastry", "pies and tarts", "preps", "preserve", "salad",
                    "seafood", "side dish", "soup", "special occasions", "starter", "sweets"
                )),
                HealthFilter(meal = listOf("lunch", "dinner"))
            ))
        )


        val sections = mapOf(
            "Breakfast" to sectionBreakfast,
            "Lunch" to sectionLunch,
            "Dinner" to sectionDinner
        )

        // Create the meal plan request object
        val mealPlanRequest = MealPlanRequest(
            size = 2,
            plan = Plan(
                accept = accept,
                fit = fit,
                sections = sections
            )
        )

        // Serialize the request to JSON and log it
        val gson = com.google.gson.Gson()
        Log.d("MealPlanningFragment", "Serialized Request: ${gson.toJson(mealPlanRequest)}")

        // Fetch the meal plan
        viewModel.fetchMealPlan(mealPlanRequest)

        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                when (state) {
                    is MealPlanUIState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is MealPlanUIState.Success -> {
                        adapter.submitList(state.updatedMeals)
                        Log.d("MealPlanningFragmentt", "Sorted meals: ${state.updatedMeals}")

                        binding.progressBar.visibility = View.GONE
                    }
                    is MealPlanUIState.Error -> {
                        Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
