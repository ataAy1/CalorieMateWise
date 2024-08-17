package com.app.meal_planning.domain.usecase

import android.util.Log
import com.app.meal_planning.data.model.MealsModel
import com.app.meal_planning.domain.repository.MealPlanningRepository
import com.app.meal_planning.dto.MealPlanRequest
import com.app.meal_planning.dto.RecipeResponse
import javax.inject.Inject

class MealPlanningUseCase @Inject constructor(
    private val repository: MealPlanningRepository
) {
    suspend fun execute(request: MealPlanRequest): MealsModel {
        Log.d("MealPlanningUseCase", "Executing use case with request: $request")
        return repository.getMealPlan(request)
    }

    suspend fun getRecipeDetails(uri: String): RecipeResponse {
        Log.d("MealPlanningUseCase", "Fetching recipe details for URI: $uri")
        return repository.getRecipeByUri(uri)
    }
}
