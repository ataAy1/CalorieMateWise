package com.app.meal_planning.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.meal_planning.data.mapper.MealsMapper
import com.app.meal_planning.data.model.MealPlanningRecipe
import com.app.meal_planning.domain.usecase.MealPlanningUseCase
import com.app.meal_planning.data.model.MealsModel
import com.app.meal_planning.dto.MealPlanRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

@HiltViewModel
class MealPlanningViewModel @Inject constructor(
    private val useCase: MealPlanningUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MealPlanUIState>(MealPlanUIState.Loading)
    val uiState: StateFlow<MealPlanUIState> get() = _uiState

    fun fetchMealPlan(request: MealPlanRequest) {
        viewModelScope.launch {
            try {
                val mealsModel = useCase.execute(request)
                val sortedMeals = mealsModel.meals
                Log.d("MealPlanningViewModel", "Meals model received: $mealsModel")
                Log.d("MealPlanningViewModel", "Sorted meals: $sortedMeals")

                val allMeals = Collections.synchronizedList(mutableListOf<MealPlanningRecipe>())
                val recipeFetchJobs = sortedMeals.map { meal ->
                    val mealUri = meal.linkOfFood
                    val mealType = meal.mealType

                    if (mealUri != null && mealType != null) {
                        async {
                            val recipes = getRecipeDetails(mealUri, mealType)
                            recipes?.let { allMeals.addAll(it) }
                        }
                    } else {
                        null
                    }
                }.filterNotNull()

                recipeFetchJobs.awaitAll()

                _uiState.value = MealPlanUIState.Success(allMeals)

            } catch (e: Exception) {
                Log.e("MealPlanningViewModel", "Exception: ", e)
                _uiState.value = MealPlanUIState.Error("Failed to load meal plan: ${e.message}")
            }
        }
    }

    private suspend fun getRecipeDetails(uri: String, mealType: String): List<MealPlanningRecipe>? {
        return try {
            val recipeResponse = useCase.getRecipeDetails(uri)
            Log.d("MealPlanningViewModel", "Recipe response: $recipeResponse")

            recipeResponse.hits.map { hit ->
                val recipe = hit.recipe
                MealPlanningRecipe(
                    mealType = mealType,
                    linkOfFood = uri,
                    imageUrl = recipe.image,
                    label = recipe.label,
                    calories = recipe.calories.toInt(),
                    yield = recipe.yield.toInt()

                )
            }
        } catch (e: Exception) {
            Log.e("MealPlanningViewModel", "Exception: ", e)
            null
        }
    }
}
