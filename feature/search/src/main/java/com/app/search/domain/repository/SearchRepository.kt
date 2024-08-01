package com.app.search.domain.repository

import com.app.search.data.model.ApiResponse
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun searchFood(query: String): Flow<ApiResponse>
}
