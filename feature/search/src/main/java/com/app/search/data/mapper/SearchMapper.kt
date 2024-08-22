package com.app.search.data.mapper

import android.util.Log
import com.app.data.dto.ParsedFood


class SearchMapper {

    fun mapFoodToParsedFood(food: com.app.data.dto.Food): ParsedFood {
        Log.d("SearchMapper", "API response: ")

        return ParsedFood(
            foodId = food.foodId,
            label = food.label,
            knownAs = food.knownAs,
            nutrients = food.nutrients,
            category = food.category,
            categoryLabel = food.categoryLabel,
            image = food.image ?: ""
        )
        Log.d("SearchMapper", "API response: ")

    }

}
