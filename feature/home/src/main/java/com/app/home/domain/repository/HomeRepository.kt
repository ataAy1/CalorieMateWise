package com.app.home.domain.repository

import com.app.core.data.model.FoodModel
import com.app.domain.model.NutritionResult
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getTodayFoods(): Flow<List<FoodModel>>
    fun getAllFoods(): Flow<List<FoodModel>>
    suspend fun deleteFood(foodId: String)
    fun getAnalysisData(): Flow<NutritionResult>


}
