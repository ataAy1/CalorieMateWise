package com.app.meal_planning.domain.repository

import android.content.Context
import com.app.meal_planning.data.model.MealPlanUpload
import com.app.meal_planning.data.model.MealsModel
import com.app.meal_planning.dto.MealPlanRequest
import com.app.meal_planning.dto.RecipeResponse

interface MealPlanningRepository {
    suspend fun getMealPlan(request: MealPlanRequest): MealsModel
    suspend fun getRecipeByUri(uri: String): RecipeResponse
    suspend fun uploadMealPlans(mealPlan: List<MealPlanUpload>,context: Context)
    suspend fun getUserCount(): String
    suspend fun updateUserCount(newCountDate: String)

}
