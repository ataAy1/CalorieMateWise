package com.app.meal_planning.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.meal_planning.data.model.MealPlanningRecipe
import com.app.meal_planning.databinding.ItemMealPlanningBinding

class MealAdapter : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var mealList: List<MealPlanningRecipe> = emptyList()

    fun submitList(list: List<MealPlanningRecipe>) {
        mealList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealPlanningBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val meal = mealList[position]

        holder.itemView.setOnClickListener {
            // val action = MealFragmentDirections.actionMealFragmentToMealDetailFragment(meal)
            // navController.navigate(action)
        }

        val calories = meal.calories ?: 600
        val yield = meal.yield ?: 3
        val caloriesPerServing = calories.toDouble() / yield.toDouble()

        val mealsPerDay = 3
        val totalMeals = mealList.size
        val days = (totalMeals + mealsPerDay - 1) / mealsPerDay

        val dayIndex = (position / mealsPerDay) + 1

        holder.binding.textViewDayOfMeal.text = "$dayIndex.Gün"
        holder.binding.textViewMealLabel.text = meal.label
        holder.binding.imageViewMeal.load(meal.imageUrl) {
            crossfade(true)
        }
    }

    override fun getItemCount(): Int = mealList.size

    class MealViewHolder(val binding: ItemMealPlanningBinding) : RecyclerView.ViewHolder(binding.root)
}
