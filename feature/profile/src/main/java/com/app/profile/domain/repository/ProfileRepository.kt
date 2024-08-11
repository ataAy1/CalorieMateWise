package com.app.profile.domain.repository

import com.app.core.data.model.FoodModel
import com.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getAllFoods(): Flow<List<FoodModel>>
    fun getUserInfo(): Flow<User>
    suspend fun updateUserInfo(height: String, weight: String, age: String)

}
