package com.app.meal_planning.data.repository


import android.util.Log
import com.app.meal_planning.data.mapper.MealsMapper
import com.app.meal_planning.data.remote.MealPlanningApi
import com.app.meal_planning.domain.repository.MealPlanningRepository
import com.app.meal_planning.dto.MealPlanRequest
import com.app.meal_planning.data.model.MealsModel
import com.app.meal_planning.dto.RecipeResponse
import javax.inject.Inject
import retrofit2.Response

class MealPlanningRepositoryImpl @Inject constructor(
    private val api: MealPlanningApi,
    private val mapper: MealsMapper
) : MealPlanningRepository {

    override suspend fun getMealPlan(request: MealPlanRequest): MealsModel {
        Log.d("MealPlanningRepositoryImpl", "Sending request: $request")
        val response = api.getMealPlan(
            appId = "490834e2",
            userId = "micheal708",
            authHeader = "Basic NDkwODM0ZTI6NGRiYmI4ZjY2YjlmMDk1ODJjOGRkMmQ3OGNjMTcyOGY=",
            request = request
        )

        if (response.isSuccessful) {
            val responseBody = response.body() ?: throw Exception("Response body is null")
            Log.d("MealPlanningRepositoryImpl", "Response received: $responseBody")
            return mapper.mapToMealsModel(responseBody)
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            Log.e("MealPlanningRepositoryImpl", "API call failed with code ${response.code()}: $errorBody")
            throw Exception("API call failed with code ${response.code()}: $errorBody")
        }

    }

    override suspend fun getRecipeByUri(uri: String): RecipeResponse {
        Log.d("MealPlanningRepositoryImpl", "Fetching recipe details for URI: $uri")
        val fieldsArray = listOf("label", "image", "calories", "mealType","yield")

        val response = api.getRecipeByUri(
            type = "public",
            beta = true,
            uri = uri,
            appId = "490834e2",
            appKey = "4dbbb8f66b9f09582c8dd2d78cc1728f",
            fields = fieldsArray
        )

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body is null")
            Log.e("ResponseBody", "ResponseBody ${response.code()}: ${response.body()}")

        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            Log.e("MealPlanningRepositoryImpl", "API call failed with code ${response.code()}: $errorBody")
            throw Exception("API call failed with code ${response.code()}: $errorBody")
        }
    }

}