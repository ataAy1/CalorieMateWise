package com.app.detail.domain.usecase

import android.net.Uri
import com.app.detail.data.model.FoodModel
import com.app.detail.domain.repository.SearchDetailRepository
import javax.inject.Inject

class SearchDetailUseCase @Inject constructor(
    private val repository: SearchDetailRepository
) {
    suspend fun addFoodToMeal(food: FoodModel) {
        repository.addFoodToMeal(food)
    }

    suspend fun uploadImage(imageUri: Uri): String {
        return repository.uploadImage(imageUri)
    }
}
