package com.app.meal_planning.presentation

import com.app.meal_planning.data.model.MealPlanningRecipe

sealed class MealPlanUIState {
    object Loading : MealPlanUIState()
    data class Success(val updatedMeals: List<MealPlanningRecipe>) : MealPlanUIState()
    data class Error(val message: String) : MealPlanUIState()

    object Uploading : MealPlanUIState()
    object UploadSuccess : MealPlanUIState()
    data class UploadError(val message: String) : MealPlanUIState()
}
