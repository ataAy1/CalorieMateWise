package com.app.detail.domain.repository

import com.app.detail.data.model.FoodModel

interface SearchDetailRepository {
    suspend fun addFoodToMeal(food: FoodModel)
}
