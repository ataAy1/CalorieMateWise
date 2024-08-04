package com.app.data.mapper

import com.app.data.dto.Food
import com.app.data.dto.ParsedFood

class SearchMapper {
    fun mapFoodToParsedFood(food: Food): ParsedFood {
        return ParsedFood(
            foodId = food.foodId,
            label = food.label,
            knownAs = food.knownAs,
            nutrients = food.nutrients,
            category = food.category,
            categoryLabel = food.categoryLabel,
            image = food.image ?: ""
        )
    }

}
