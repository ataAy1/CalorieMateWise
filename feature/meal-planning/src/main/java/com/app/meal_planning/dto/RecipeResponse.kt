package com.app.meal_planning.dto

data class RecipeResponse(
    val hits: List<Hit>
) {
    data class Hit(
        val recipe: Recipe
    )

    data class Recipe(
        val label: String,
        val image: String,
        val calories: Double,
        val yield: Int

    )
}
