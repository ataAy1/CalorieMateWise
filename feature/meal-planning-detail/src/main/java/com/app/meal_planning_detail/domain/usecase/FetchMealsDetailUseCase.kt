package com.app.meal_planning_detail.domain.usecase

import com.app.domain.model.MealPlanUpload
import com.app.meal_planning_detail.domain.model.MealSet
import com.app.meal_planning_detail.domain.repository.MealPlansRepository
import javax.inject.Inject

class FetchMealsDetailUseCase @Inject constructor(
    private val mealPlansRepository: MealPlansRepository
) {
    suspend fun fetchMealPlanDates(): List<String>? {
        return mealPlansRepository.fetchMealPlanDates()
    }

    suspend fun fetchMealPlans(date: String, mealSetID: String): List<MealPlanUpload>? {
        return mealPlansRepository.fetchMealPlans(date, mealSetID)
    }

    suspend fun fetchMealSets(date: String): List<MealSet>? {
        return mealPlansRepository.fetchMealSets(date)
    }
}
