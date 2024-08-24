package com.app.home.domain.usecase

import com.app.home.domain.repository.HomeRepository
import javax.inject.Inject

class DeleteFoodUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {
    suspend operator fun invoke(foodId: String) {
        homeRepository.deleteFood(foodId)
    }
}
