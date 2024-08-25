package com.app.search.data.repository

import android.util.Log
import com.app.data.dto.ApiResponse
import com.app.search.data.remote.FoodDatabaseApi
import com.app.search.domain.repository.SearchRepository
import com.app.utils.TranslationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: FoodDatabaseApi
) : SearchRepository {
    override fun searchFood(query: String): Flow<ApiResponse> = flow {
        val response = apiService.searchFood(
            appId = Constants.APP_ID,
            appKey = Constants.APP_KEY,
            query = query,
            nutritionType = Constants.NUTRITION_TYPE
        )
        emit(response)
    }.onStart {
        Log.d("SearchRepositoryImpl", "Search started for query: $query")
    }.catch { e ->
        Log.e("SearchRepositoryImpl", "Error fetching food data", e)
        throw e
    }
}