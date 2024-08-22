package com.app.profile.domain.repository

import com.app.core.data.model.FoodModel
import com.app.domain.model.NutritionResult
import com.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getAllFoods(): Flow<List<FoodModel>>
    suspend fun getUserInfo(): Flow<User>
    suspend fun updateUserInfo(height: String, weight: String, age: String)
    suspend fun saveNutritionAnalysis(userId: String, result: NutritionResult)
    suspend fun logOut()
}
