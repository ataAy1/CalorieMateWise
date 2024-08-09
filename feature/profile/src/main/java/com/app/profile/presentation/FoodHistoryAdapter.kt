package com.app.profile.presentation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.app.core.data.model.FoodModelParcelize
import com.app.profile.databinding.ItemHistoryMealsBinding

class FoodHistoryAdapter(private val foodList: Array<FoodModelParcelize>) :
    RecyclerView.Adapter<FoodHistoryAdapter.FoodViewHolder>() {

    inner class FoodViewHolder(private val binding: ItemHistoryMealsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(food: FoodModelParcelize) {
            binding.foodCalorieTextView.text = food.calories.toString()
            binding.foodProteinTextView.text = food.protein.toString()
            binding.foodFatTextView.text = food.fat.toString()
            binding.foodCarbohydrateTextView.text = food.carbohydrates.toString()
            binding.fooodNameTextView.text=food.label.toString()
            binding.foodImageView.load(food.image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding =
            ItemHistoryMealsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        holder.bind(foodList[position])
    }

    override fun getItemCount() = foodList.size
}
