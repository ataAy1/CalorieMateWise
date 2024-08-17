package com.app.meal_planning.data.model

data class MealPlanResponse(
    val status: String,
    val selection: List<MealSelection>
)

data class MealSelection(
    val sections: MealSections
)

data class MealSections(
    val Breakfast: MealAssigned,
    val Lunch: MealAssigned,
    val Dinner: MealAssigned
)

data class MealAssigned(
    val assigned: String
)
