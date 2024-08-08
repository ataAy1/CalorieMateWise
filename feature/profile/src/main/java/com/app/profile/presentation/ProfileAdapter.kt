package com.app.profile.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.data.model.FoodModelParcelize
import com.app.profile.databinding.ItemProfileHistoryBinding

class ProfileAdapter(
    private val onItemClick: (String, List<FoodModelParcelize>) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.FoodViewHolder>() {

    private var groupedData: Map<String, List<FoodModelParcelize>> = emptyMap()

    fun updateData(newFoods: List<FoodModelParcelize>) {
        Log.d("ProfileAdapter", "Updating data with newFoods: $newFoods")
        groupedData = newFoods.groupBy { it.date }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemProfileHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val date = groupedData.keys.elementAt(position)
        val foods = groupedData[date] ?: emptyList()
        Log.d("ProfileAdapter", "Binding data at position $position with date: $date and foods: $foods")
        holder.bind(date, foods)
    }

    override fun getItemCount() = groupedData.size

    inner class FoodViewHolder(private val binding: ItemProfileHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String, foods: List<FoodModelParcelize>) {
            val totalCalories = foods.sumOf { it.calories }
            binding.dayOfMonthTextView.text = date
            binding.totalCaloriesTextView.text = "$totalCalories calories"

            binding.root.setOnClickListener {
                onItemClick(date, foods)
            }
        }
    }
}
