package com.app.meal_planning_detail.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.search_interactive.databinding.ItemMealPlanningDetailBinding
import com.app.domain.model.MealPlanUpload

class MealPlanningDetailAdapter :
    ListAdapter<MealPlanUpload, MealPlanningDetailAdapter.MealPlanViewHolder>(MealPlanDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPlanViewHolder {
        val binding = ItemMealPlanningDetailBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealPlanViewHolder, position: Int) {
        val mealPlan = getItem(position)
        holder.bind(mealPlan, position)
    }

    class MealPlanViewHolder(private val binding: ItemMealPlanningDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val mealsPerDay = 3

        fun bind(mealPlan: MealPlanUpload, position: Int) {
            binding.textViewLabel.text = mealPlan.label

            val caloriesPerYield = if (mealPlan.yield > 0) {
                mealPlan.calories / mealPlan.yield
            } else {
                0
            }
            binding.textViewCalories.text = "Kalori: ${caloriesPerYield}"
            binding.imageViewMeal.load(mealPlan.imageUrl)

            val day = (position / mealsPerDay) + 1
            binding.mealOfDay.text = "$day.Gün Öğününüz"
        }
    }

    class MealPlanDiffCallback : DiffUtil.ItemCallback<MealPlanUpload>() {
        override fun areItemsTheSame(oldItem: MealPlanUpload, newItem: MealPlanUpload): Boolean {
            return oldItem.linkOfFood == newItem.linkOfFood
        }

        override fun areContentsTheSame(oldItem: MealPlanUpload, newItem: MealPlanUpload): Boolean {
            return oldItem == newItem
        }
    }
}
