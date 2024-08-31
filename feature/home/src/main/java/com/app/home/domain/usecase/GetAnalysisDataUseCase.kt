package com.app.home.domain.usecase

import com.app.domain.model.NutritionResult
import com.app.home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAnalysisDataUseCase @Inject constructor(
    private val repository: HomeRepository
) {
    operator fun invoke(): Flow<NutritionResult> {
        return repository.getAnalysisData()
    }
}
