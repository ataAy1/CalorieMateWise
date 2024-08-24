package com.app.search.data.mapper

import android.util.Log
import com.app.data.dto.Food
import com.app.data.dto.ParsedFood
import com.app.utils.TranslationUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchMapper {

    suspend fun mapFoodToParsedFood(food: Food): ParsedFood {
        Log.d("SearchMapper", "Mapping Food to ParsedFood")

        val label = withContext(Dispatchers.IO) {
            TranslationUtil.translateToTurkish(food.label ?: "")
        }

        return ParsedFood(
            foodId = food.foodId,
            label = label,
            knownAs = food.knownAs,
            nutrients = food.nutrients,
            category = food.category,
            categoryLabel = food.categoryLabel,
            image = food.image ?: ""
        )
    }

}
