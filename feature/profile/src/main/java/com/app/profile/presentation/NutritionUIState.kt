package com.app.profile.presentation

data class NutritionUIState(
    val calories: Int = 0,
    val protein: Int = 0,
    val fat: Int = 0,
    val carbs: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
