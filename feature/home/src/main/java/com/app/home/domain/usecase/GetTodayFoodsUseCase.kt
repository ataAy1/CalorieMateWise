package com.app.home.domain.usecase

import com.app.core.data.model.FoodModel
import com.app.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTodayFoodsUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<List<FoodModel>> {
        return repository.getTodayFoods()
    }
}