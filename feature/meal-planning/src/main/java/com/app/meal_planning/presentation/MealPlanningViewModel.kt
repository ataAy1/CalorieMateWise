package com.app.meal_planning.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.meal_planning.data.model.MealPlanUpload
import com.app.meal_planning.data.model.MealPlanningRecipe
import com.app.meal_planning.domain.usecase.MealPlanningUseCase
import com.app.meal_planning.domain.usecase.UploadMealPlanUseCase
import com.app.meal_planning.data.model.MealsModel
import com.app.meal_planning.domain.usecase.UserCounterUseCase
import com.app.meal_planning.dto.MealPlanRequest
import com.app.utils.ImageUtil
import com.google.firebase.ktx.Firebase
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
    private val useCase: MealPlanningUseCase,
    private val uploadUseCase: UploadMealPlanUseCase,
    private val userCounterUseCase: UserCounterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<MealPlanUIState>(MealPlanUIState.Loading)
    val uiState: StateFlow<MealPlanUIState> get() = _uiState

    fun fetchMealPlan(request: MealPlanRequest) {
        viewModelScope.launch {
            _uiState.value = MealPlanUIState.Loading
            try {
                val mealsModel = useCase.execute(request)
                val orderedMeals = mealsModel.meals
                Log.d("MealPlanningViewModel3", "Meals model received: $mealsModel")
                Log.d("MealPlanningViewModel4", "Ordered meals: $orderedMeals")

                val allMeals = Collections.synchronizedList(mutableListOf<MealPlanningRecipe>())

                val recipeFetchJobs = orderedMeals.map { meal ->
                    async {
                        val mealUri = meal.linkOfFood
                        val mealType = meal.mealType
                        Log.d("MealPlanningViewModel14", "mealUri meals: $mealUri")

                        if (mealUri != null && mealType != null) {
                            val recipes = getRecipeDetails(mealUri, mealType)
                            recipes?.let { allMeals.addAll(it) }
                        }
                    }
                }

                recipeFetchJobs.awaitAll()

                _uiState.value = MealPlanUIState.Success(allMeals)

            } catch (e: Exception) {
                Log.e("MealPlanningViewModel1", "Exception: ", e)
                _uiState.value = MealPlanUIState.Error("Failed to load meal plan: ${e.message}")
            }
        }
    }

    private suspend fun getRecipeDetails(uri: String, mealType: String): List<MealPlanningRecipe>? {
        return try {
            Log.d("MealPlanningViewModel23", "Fetching recipe details for URI: $uri")
            val recipeResponse = useCase.getRecipeDetails(uri)
            Log.d("MealPlanningViewModel2", "Recipe response for URI $uri: $recipeResponse")

            recipeResponse.hits.map { hit ->
                val recipe = hit.recipe
                MealPlanningRecipe(
                    mealType = mealType,
                    linkOfFood = uri,
                    imageUrl = recipe.image,
                    label = recipe.label,
                    calories = recipe.calories.toInt(),
                    yield = recipe.yield.toInt(),
                    timestamp = System.currentTimeMillis()

                )
            }
        } catch (e: Exception) {
            Log.e("MealPlanningViewModel", "Exception during getRecipeDetails for URI: $uri", e)
            null
        }
    }


    fun uploadMealPlans(mealPlans: List<MealPlanUpload>, context: Context) {
        viewModelScope.launch {
            try {
                uploadUseCase.execute(mealPlans,context)
            } catch (e: Exception) {
            }
        }
    }


    fun fetchUserCount() {
        viewModelScope.launch {
            _uiState.value = MealPlanUIState.Loading
            try {
                val count = userCounterUseCase.getUserCount()
                _uiState.value = MealPlanUIState.UserCountDateFetched(count)
                Log.d("fetchUserCount", "User count fetched: $count")
            } catch (e: Exception) {
                _uiState.value = MealPlanUIState.UserCountError("Error fetching user count: ${e.message}")
                Log.e("fetchUserCount", "Error fetching user count", e)
            }
        }
    }

    fun updateUserCount(newCountDate: String) {
        viewModelScope.launch {
            try {
                userCounterUseCase.updateUserCount(newCountDate)
                Log.d("fetchUserCount", "User count updated to: $newCountDate")
            } catch (e: Exception) {
                Log.e("fetchUserCount", "Error updating user count", e)
            }
        }
    }
}