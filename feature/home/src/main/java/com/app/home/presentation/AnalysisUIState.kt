package com.app.home.presentation

import com.app.domain.model.NutritionResult

data class AnalysisUIState(
    val nutritionResult: NutritionResult? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
