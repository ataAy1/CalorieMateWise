package com.app.search.data.mapper

import com.app.search.data.model.Food
import com.app.search.data.model.ParsedFood

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
