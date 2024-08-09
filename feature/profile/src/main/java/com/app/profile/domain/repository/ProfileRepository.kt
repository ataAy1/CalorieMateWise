package com.app.profile.domain.repository

import com.app.core.data.model.FoodModel
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getAllFoods(): Flow<List<FoodModel>>
}
