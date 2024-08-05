package com.app.home.domain.usecase

import com.app.core.data.model.FoodModel
import com.app.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllFoodsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<List<FoodModel>> {
        return repository.getAllFoods()
    }
}
