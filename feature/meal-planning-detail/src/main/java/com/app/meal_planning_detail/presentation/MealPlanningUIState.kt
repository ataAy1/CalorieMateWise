package com.app.meal_planning_detail.presentation

import com.app.domain.model.MealPlanUpload
import com.app.meal_planning_detail.domain.model.MealSet

data class MealPlanningUIState(
    val mealPlanDates: List<String> = emptyList(),
    val mealPlans: List<MealPlanUpload> = emptyList(),
    val mealSets: List<MealSet> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
