package com.app.profile.presentation

import com.app.domain.model.NutritionResult

data class NutritionUIState(
    val nutritionResult: NutritionResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
)
