package com.app.search.data.mapper

import android.util.Log
import com.app.data.dto.Food
import com.app.data.dto.ParsedFood
import com.app.utils.TranslationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchMapper {

    suspend fun mapFoodToParsedFood(food: Food, availableImages: List<String>): ParsedFood {
        Log.d("SearchMapper", "Mapping Food to ParsedFood")

        val label = withContext(Dispatchers.IO) {
            TranslationUtil.translateToTurkish(food.label ?: "")
        } ?: ""

        val image: String = if (food.image.isNullOrEmpty()) {
            val randomImage = availableImages.randomOrNull() ?: ""
            Log.d("SearchMapper", "No image provided for foodId: ${food.foodId}, using random image: $randomImage")
            randomImage
        } else {
            food.image
        }

        Log.d("SearchMapper", "FoodId: ${food.foodId}, Image: $image")

        return ParsedFood(
            foodId = food.foodId,
            label = label,
            knownAs = food.knownAs,
            nutrients = food.nutrients,
            category = food.category,
            categoryLabel = food.categoryLabel,
            image = image
        )
    }

}
