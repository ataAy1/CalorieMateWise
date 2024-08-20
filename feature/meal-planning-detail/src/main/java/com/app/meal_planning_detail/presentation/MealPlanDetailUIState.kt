package com.app.meal_planning_detail.presentation

import com.app.domain.model.MealPlanUpload

data class MealPlanDetailUIState (
    val combinedResponse: List<MealPlanUpload> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

