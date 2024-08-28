package com.app.meal_planning_detail.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.app.search_interactive.databinding.ItemMealPlanningListBinding
import android.util.Log

class MealPlanningListAdapter(private val onClick: (String, String) -> Unit) :
    ListAdapter<String, MealPlanningListAdapter.DateViewHolder>(DateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemMealPlanningListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = getItem(position)
        holder.bind(date)
    }

    inner class DateViewHolder(private val binding: ItemMealPlanningListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(date: String) {
            binding.dateOfMeal.text = "$date -Yemek Programınız"
            binding.root.setOnClickListener {
                onClick(date, binding.dateOfMeal.text.toString())
            }
        }
    }

    private class DateDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}
