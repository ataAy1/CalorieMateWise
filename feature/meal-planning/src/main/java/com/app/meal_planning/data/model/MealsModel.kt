package com.app.meal_planning.data.model

data class MealsModel(
    val meals: List<Meal>
)

data class Meal(
    val mealType: String,
    val linkOfFood: String,
)

data class MealPlanningRecipe(
    val mealType: String,
    val linkOfFood: String,
    val imageUrl: String?,
    val label: String?,
    val calories: Int?,
    val yield: Int?,
    val timestamp: Long = System.currentTimeMillis()
)

data class DayMeals(
    val breakfast: Meal,
    val lunch: Meal,
    val dinner: Meal
)
