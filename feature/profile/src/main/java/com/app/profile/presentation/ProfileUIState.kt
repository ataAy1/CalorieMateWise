package com.app.profile.presentation

import com.app.core.data.model.FoodModel
import com.app.domain.model.User

data class ProfileUIState(
    val foodList: List<FoodModel> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
