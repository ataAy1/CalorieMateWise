package com.app.meal_planning.dto

data class MealPlanRequest(
    val size: Int,
    val plan: Plan
)

data class Plan(
    val accept: Accept,
    val fit: Fit,
    val sections: Map<String, SectionDetail>
)

data class Accept(
    val all: List<HealthFilter>
)

data class HealthFilter(
    val health: List<String>? = null,
    val dish: List<String>? = null,
    val meal: List<String>? = null
)

data class Fit(
    val ENERC_KCAL: NutrientRange
)

data class NutrientRange(
    val min: Int? = null,
    val max: Int? = null
)

data class SectionDetail(
    val accept: Accept? = null
)