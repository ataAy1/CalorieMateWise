package com.app.meal_planning.data.remote

import com.app.meal_planning.data.model.MealPlanResponse
import com.app.meal_planning.dto.MealPlanRequest
import com.app.meal_planning.dto.RecipeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface MealPlanningApi {

    @GET("api/recipes/v2/by-uri")
    suspend fun getRecipeByUri(
        @Query("type") type: String = "public",
        @Query("beta") beta: Boolean = true,
        @Query("uri") uri: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String,
        @Query("field") fields: List<String>
    ): Response<RecipeResponse>



    @POST("api/meal-planner/v1/{app_id}/select")
    suspend fun getMealPlan(
        @Path("app_id") appId: String,
        @Body request: MealPlanRequest,
        @Header("Edamam-Account-User") userId: String,
        @Header("Authorization") authHeader: String
    ): Response<MealPlanResponse>


    }


