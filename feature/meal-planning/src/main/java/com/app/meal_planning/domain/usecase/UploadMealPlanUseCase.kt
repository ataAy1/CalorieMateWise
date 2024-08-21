// domain/usecase/UploadMealPlanUseCase.kt
package com.app.meal_planning.domain.usecase

import android.content.Context
import com.app.meal_planning.data.model.MealPlanUpload
import com.app.meal_planning.domain.repository.MealPlanningRepository
import javax.inject.Inject

class UploadMealPlanUseCase @Inject constructor(
    private val repository: MealPlanningRepository
) {
    suspend fun execute(mealPlans: List<MealPlanUpload>,context: Context) {
        repository.uploadMealPlans(mealPlans,context)
    }
}
