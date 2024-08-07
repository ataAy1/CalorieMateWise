package com.app.home.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.app.home.databinding.FragmentFoodItemsDialogBinding

class FoodItemsDialogFragment : DialogFragment() {

    private var _binding: FragmentFoodItemsDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var date: String
    private var totalCalories: Double = 0.0
    private var totalFat: Double = 0.0
    private var totalFoodCarb: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFoodItemsDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            date = it.getString("DATE") ?: ""
            totalCalories = it.getDouble("TOTAL_CALORIES", 0.0)
            totalFat = it.getDouble("TOTAL_FAT", 0.0)
            totalFoodCarb = it.getInt("TOTAL_FOOD_COUNT", 0)
        }

        binding.dateTextView.text = date
        binding.totalCaloriesTextView.text = "Total Calories: $totalCalories"
        binding.totalFatTextView.text = "Total Fat: $totalFat"
        binding.totalCarbTextView.text = "Total Carb : $totalFoodCarb"
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(date: String, totalCalories: Double, totalFat: Double, totalFoodCount: Int): FoodItemsDialogFragment {
            return FoodItemsDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("DATE", date)
                    putDouble("TOTAL_CALORIES", totalCalories)
                    putDouble("TOTAL_FAT", totalFat)
                    putInt("TOTAL_FOOD_COUNT", totalFoodCount)
                }
            }
        }
    }
}
