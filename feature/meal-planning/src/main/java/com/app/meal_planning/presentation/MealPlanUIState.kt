package com.app.meal_planning.presentation

import com.app.meal_planning.data.model.MealPlanningRecipe
import com.app.meal_planning.data.model.MealsModel
import com.app.meal_planning.dto.RecipeResponse

sealed class MealPlanUIState {
    object Loading : MealPlanUIState()
    data class Success(val updatedMeals: List<MealPlanningRecipe>) : MealPlanUIState()
    data class Error(val message: String) : MealPlanUIState()
}
