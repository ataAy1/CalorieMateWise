package com.app.detail.domain.repository

import android.net.Uri
import com.app.detail.data.model.FoodModel

interface SearchDetailRepository {
    suspend fun addFoodToMeal(food: FoodModel)
    suspend fun uploadImage(imageUri: Uri): String

}
