package com.app.profile.domain.usecase

import com.app.core.data.model.FoodModel
import com.app.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchFoodListUseCase @Inject constructor(
    private val repository: ProfileRepository
) {
    suspend fun execute(): Flow<List<FoodModel>> {
        return repository.getAllFoods()
    }
}
