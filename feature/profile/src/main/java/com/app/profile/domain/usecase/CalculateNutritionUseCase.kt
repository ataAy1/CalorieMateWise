package com.app.profile.domain.usecase

import com.app.domain.model.NutritionResult
import com.app.profile.domain.repository.ProfileRepository
import javax.inject.Inject

class CalculateNutritionUseCase @Inject constructor(
    private val repository: ProfileRepository
) {

    fun calculate(age: Int, height: Int, weight: Int, activityLevel: String, gender: String): NutritionResult {
        val bmr = if (gender.lowercase() == "male") {
            10 * weight + 6.25 * height - 5 * age + 5
        } else {
            10 * weight + 6.25 * height - 5 * age - 161
        }

        val activityMultiplier = when (activityLevel.lowercase()) {
            "az" -> 1.2
            "normal" -> 1.375
            "cok" -> 1.55
            else -> 1.2
        }

        val totalCalories = (bmr * activityMultiplier).toInt()

        val protein = (0.3 * totalCalories / 4).toInt()
        val fat = (0.25 * totalCalories / 9).toInt()
        val carbs = (0.45 * totalCalories / 4).toInt()

        return NutritionResult(
            calories = totalCalories,
            protein = protein,
            fat = fat,
            carbs = carbs,
            gender=gender
        )
    }

    suspend fun saveNutritionAnalysis(userId: String, result: NutritionResult) {
        repository.saveNutritionAnalysis(userId, result)
    }
}
