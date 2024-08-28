package com.app.profile.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.core.data.model.FoodModelParcelize
import com.app.profile.databinding.ItemProfileHistoryBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


class ProfileAdapter(
    private val onItemClick: (String, List<FoodModelParcelize>) -> Unit
) : RecyclerView.Adapter<ProfileAdapter.FoodViewHolder>() {

    private var groupedData: List<Pair<String, List<FoodModelParcelize>>> = emptyList()


    fun updateData(newFoods: List<FoodModelParcelize>) {
        Log.d("ProfileAdapter", "Updating data with newFoods: $newFoods")

        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("tr"))

        groupedData = newFoods.groupBy { LocalDate.parse(it.date, formatter) }
            .toList()
            .sortedByDescending { it.first }
            .map { it.first.format(formatter) to it.second }

        notifyDataSetChanged()
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemProfileHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val (date, foods) = groupedData[position]
        Log.d("ProfileAdapter", "Binding data at position $position with date: $date and foods: $foods")
        holder.bind(date, foods)
    }

    override fun getItemCount() = groupedData.size

    inner class FoodViewHolder(private val binding: ItemProfileHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String, foods: List<FoodModelParcelize>) {
            val totalCalories = foods.sumOf { it.calories }
            binding.dayOfMonthTextView.text = date
            binding.totalCaloriesTextView.text = "$totalCalories kalori"

            binding.historyImageView.setOnClickListener {
                onItemClick(date, foods)
            }
        }
    }
}
