// File: SearchRepositoryImpl.kt
package com.app.search.data.repository

import android.util.Log
import com.app.search.data.model.ApiResponse
import com.app.search.data.remote.FoodDatabaseApi
import com.app.search.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: FoodDatabaseApi
) : SearchRepository {
    override fun searchFood(query: String): Flow<com.app.data.dto.ApiResponse> = flow {
        val response = apiService.searchFood(
            appId = Constants.APP_ID,
            appKey = Constants.APP_KEY,
            query = query,
            nutritionType = Constants.NUTRITION_TYPE
        )
        Log.d("SearchRepositoryImpl", "Full response: $response")
        emit(response)
    }.onStart {
        // Optional: Do something before the flow starts
    }.catch { e ->
        Log.e("SearchRepositoryImpl", "Error fetching food data", e)
        emit(com.app.data.dto.ApiResponse(text = "Error fetching food data", parsed = emptyList(), hints = emptyList()))
    }
}
