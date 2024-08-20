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
import com.app.meal_planning.data.model.MealPlanResponse
import com.app.meal_planning.data.model.MealPlanUpload
import com.app.meal_planning.data.model.MealPlanningRecipe
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
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var mealAdapter: MealAdapter

    private var userInputs = mutableMapOf<String, String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMealPlanningBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatAdapter = ChatAdapter()
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewChat.adapter = chatAdapter

        mealAdapter = MealAdapter()
        binding.recyclerViewMealPlanning.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewMealPlanning.adapter = mealAdapter


        startChatFlow()
    }

    private fun startChatFlow() {
        chatAdapter.promptForInput("How many calories do you want?")

        binding.buttonSend.setOnClickListener {
            val userInput = binding.editTextMessage.text.toString()
            if (userInput.isNotBlank()) {
                chatAdapter.addMessage("You: $userInput")
                handleUserResponse(userInput)
                binding.editTextMessage.text.clear()
            }
        }
    }

    private fun handleUserResponse(response: String) {
        when {
            !userInputs.containsKey("calories") -> {
                Log.d("ChatDebug", "Received calories input: $response")
                val calorieRange = parseCalorieRange(response)
                if (calorieRange != null) {
                    userInputs["calories"] = response
                    chatAdapter.promptForInput("How many days for the meal plan?")
                } else {
                    Toast.makeText(requireContext(), "Invalid input! Please enter a valid calorie range (e.g., 1500-2500).", Toast.LENGTH_SHORT).show()
                }
            }
            !userInputs.containsKey("days") -> {
                Log.d("ChatDebug", "Received days input: $response")
                val days = response.toIntOrNull()
                if (days != null && days > 0) {
                    userInputs["days"] = response
                    chatAdapter.promptForInput("Please list all items you want to include for breakfast, lunch, and dinner (e.g., pancake, bread, salad).")
                } else {
                    Toast.makeText(requireContext(), "Invalid input! Please enter a valid number of days (e.g., 3).", Toast.LENGTH_SHORT).show()
                }
            }
            userInputs.containsKey("days") && !userInputs.containsKey("items") -> {
                Log.d("ChatDebug", "Received meal items input: $response")
                userInputs["items"] = response
                parseMealItems(response)
            }
        }
    }


    private fun parseMealItems(input: String) {
        val items = input.split(",").map { it.trim() }
        val sections = mutableMapOf<String, SectionDetail>()

        val validDishes = listOf(
            "pizza", "sandwiches", "biscuits and cookies", "bread", "cereals",
            "condiments and sauces", "desserts", "drinks", "egg", "ice cream and custard",
            "main course", "pastry", "pies and tarts", "preps", "preserve", "salad",
            "seafood", "side dish", "soup", "special occasions", "starter", "sweets"
        )

        val invalidItems = items.filterNot { it in validDishes }

        if (invalidItems.isNotEmpty()) {
            var setFoodItems=validDishes

            val sections = mutableMapOf<String, SectionDetail>()
            sections["Breakfast"] = SectionDetail(
                accept = Accept(all = listOf(HealthFilter(dish =setFoodItems)))
            )
            sections["Lunch"] = SectionDetail(
                accept = Accept(all = listOf(HealthFilter(dish =setFoodItems)))
            )
            sections["Dinner"] = SectionDetail(
                accept = Accept(all = listOf(HealthFilter(dish =setFoodItems)))
            )
            val gson = com.google.gson.Gson()
            userInputs["sections"] = gson.toJson(sections)
            createMealPlanRequest()
        } else {
            val sections = mutableMapOf<String, SectionDetail>()
            sections["Breakfast"] = SectionDetail(
                accept = Accept(all = listOf(HealthFilter(dish =items)))
            )
            sections["Lunch"] = SectionDetail(
                accept = Accept(all = listOf(HealthFilter(dish =items)))
            )
            sections["Dinner"] = SectionDetail(
                accept = Accept(all = listOf(HealthFilter(dish =items)))
            )

            val gson = com.google.gson.Gson()
            userInputs["sections"] = gson.toJson(sections)
            createMealPlanRequest()
    }
    }

    private fun createMealPlanRequest() {
        val calorieInput = userInputs["calories"]?.let { parseCalorieRange(it) }
        val daysInput = userInputs["days"]?.toIntOrNull()
        val sections = userInputs["sections"]?.let { parseSections(it) } ?: emptyMap()

        if (calorieInput != null && daysInput != null) {
            val healthFilter = HealthFilter(health = listOf("pork-free"))
            val accept = Accept(all = listOf(healthFilter))

            val fit = Fit(
                ENERC_KCAL = NutrientRange(min = calorieInput.first, max = calorieInput.second)
            )

            val mealPlanRequest = MealPlanRequest(
                size = daysInput,
                plan = Plan(
                    accept = accept,
                    fit = fit,
                    sections = sections
                )
            )

            val gson = com.google.gson.Gson()
            Log.d("MealPlanningFragment", "Serialized Request: ${gson.toJson(mealPlanRequest)}")

            viewModel.fetchMealPlan(mealPlanRequest)

            lifecycleScope.launch {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is MealPlanUIState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is MealPlanUIState.Success -> {
                            Log.d("MealPlanningFragment", "Meals from server: ${state.updatedMeals}")
                            chatAdapter.addMessage("Meal Plan Generated Successfully!")
                            mealAdapter.submitList(state.updatedMeals)
                            binding.progressBar.visibility = View.GONE


                            val mealPlans = state.updatedMeals.mapIndexed { index, meal ->
                                transformToMealPlanUpload(meal, index + 1)
                            }


                            viewModel.uploadMealPlans(mealPlans, requireContext())
                        }
                        is MealPlanUIState.Error -> {
                            chatAdapter.addMessage("Error: ${state.message}")
                            binding.progressBar.visibility = View.GONE
                        }
                        is MealPlanUIState.Uploading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is MealPlanUIState.UploadSuccess -> {
                            chatAdapter.addMessage("Meal Plan Uploaded Successfully!")
                            binding.progressBar.visibility = View.GONE
                        }
                        is MealPlanUIState.UploadError -> {
                            chatAdapter.addMessage("Upload Error: ${state.message}")
                            binding.progressBar.visibility = View.GONE
                        }
                    }
                }
            }

        } else {
            chatAdapter.addMessage("Please enter valid inputs.")
        }
    }


    fun transformToMealPlanUpload(meal: MealPlanningRecipe, order: Int): MealPlanUpload {
        val mealPlanUpload = MealPlanUpload(
            mealType = meal.mealType ?: "Unknown",
            linkOfFood = meal.linkOfFood ?: "No Link",
            imageUrl = meal.imageUrl ?: "No Image",
            label = meal.label ?: "No Label",
            calories = meal.calories ?: 0,
            yield = meal.yield ?: 1,
            order = order
        )


        Log.d("MealPlanUpload", "Transformed MealPlanUpload: $mealPlanUpload")

        return mealPlanUpload
    }

    private fun parseCalorieRange(input: String): Pair<Int, Int>? {
        val regex = """(\d+)-(\d+)""".toRegex()
        val matchResult = regex.find(input)

        return if (matchResult != null) {
            val (min, max) = matchResult.destructured
            val minValue = min.toIntOrNull()
            val maxValue = max.toIntOrNull()
            if (minValue != null && maxValue != null && minValue <= maxValue) {
                Pair(minValue, maxValue)
            } else {
                Log.d("ChatDebug", "Invalid calorie range values: minValue=$minValue, maxValue=$maxValue")
                null
            }
        } else {
            Log.d("ChatDebug", "No match found for input: $input")
            null
        }
    }

    private fun parseSections(sectionsString: String): Map<String, SectionDetail> {
        val gson = com.google.gson.Gson()
        return try {
            val type = object : com.google.gson.reflect.TypeToken<Map<String, SectionDetail>>() {}.type
            gson.fromJson(sectionsString, type)
        } catch (e: Exception) {
            Log.e("ChatDebug", "Error parsing sections: ${e.message}")
            emptyMap()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
