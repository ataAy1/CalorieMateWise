package com.app.meal_planning.data.mapper

import com.app.meal_planning.data.model.Meal
import com.app.meal_planning.data.model.MealPlanResponse
import com.app.meal_planning.data.model.MealsModel

class MealsMapper {

    fun mapToMealsModel(response: MealPlanResponse): MealsModel {
        val meals = response.selection.flatMap { mealSelection ->
            listOf(
                Meal(
                    mealType = "Breakfast",
                    linkOfFood = mealSelection.sections.Breakfast.assigned
                ),
                Meal(
                    mealType = "Lunch",
                    linkOfFood = mealSelection.sections.Lunch.assigned
                ),
                Meal(
                    mealType = "Dinner",
                    linkOfFood = mealSelection.sections.Dinner.assigned
                )
            )
        }
        return MealsModel(meals = meals)
    }
}