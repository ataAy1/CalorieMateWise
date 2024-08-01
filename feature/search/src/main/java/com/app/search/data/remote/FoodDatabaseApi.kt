package com.app.search.data.remote

import com.app.search.data.model.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FoodDatabaseApi {
    @GET("api/food-database/v2/parser")
    suspend fun searchFood(
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("ingr") query: String,
        @Query("nutrition-type") nutritionType: String
    ): ApiResponse
}
