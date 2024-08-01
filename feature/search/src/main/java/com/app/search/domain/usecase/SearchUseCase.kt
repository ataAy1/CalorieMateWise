package com.app.search.domain.usecase

import com.app.search.data.model.ApiResponse
import com.app.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend fun execute(query: String): Flow<ApiResponse> {
        return repository.searchFood(query)
    }
}
