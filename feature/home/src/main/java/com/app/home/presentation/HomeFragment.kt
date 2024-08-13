package com.app.home.presentation

import FoodsByDateAdapter
import TodayFoodsAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.model.FoodModel
import com.app.home.R
import com.app.home.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.XAxis
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setDate()
        val todayFoodsAdapter = TodayFoodsAdapter(emptyList())
        val foodsByDateAdapter = FoodsByDateAdapter(emptyMap(), requireActivity())


        //binding.recyclerViewFoodsByDate.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewTodayFoods.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.recyclerViewTodayFoods.adapter = todayFoodsAdapter
        //binding.recyclerViewFoodsByDate.adapter = foodsByDateAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.todayFoods.collect { foods ->
                Log.d("UIUpdate", "Updating UI with today foods: $foods")
                todayFoodsAdapter.updateData(foods)
                updateBarChartAndTotalCalorie(foods)

            }
        }

        /*viewLifecycleOwner.lifecycleScope.launch {
            viewModel.allFoods.collect { foods ->
                Log.d("UIUpdate", "Updating UI with all foods: $foods")
                foodsByDateAdapter.updateData(foods)
            }
        }*/



        viewModel.getTodayFoods()
        //viewModel.getAllFoods()


    }

    private fun updateBarChartAndTotalCalorie(foods: List<FoodModel>) {
        val protein = foods.sumOf { it.protein }
        val carbohydrates = foods.sumOf { it.carbohydrates }
        val fat = foods.sumOf { it.fat }

        binding.textViewtotalCalories.text = foods.sumOf { it.calories }.toString()

        binding.textViewProtein.text = "Protein: ${protein.toString()}"
        binding.textViewCarbohydrates.text = "Karbonhidrat: ${carbohydrates.toString()}"
        binding.textViewFat.text = "Yağ: ${fat.toString()}"


        val dayOfMonth = foods.firstOrNull()?.dayOfMonth ?: "No Date"
        val yearOfMonth = foods.firstOrNull()?.yearOfMonth ?: "No Date"
        val dayName = foods.firstOrNull()?.dayName ?: ""
        val formattedDate = "$yearOfMonth-$dayOfMonth - $dayName"

        val entries = listOf(
            BarEntry(0f, protein.toFloat()),
            BarEntry(1f, carbohydrates.toFloat()),
            BarEntry(2f, fat.toFloat())
        )

        val dataSet = BarDataSet(entries, "Makro Değeleri:")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireContext(), R.color.proteinColor),
            ContextCompat.getColor(requireContext(), R.color.carbohydratesColor),
            ContextCompat.getColor(requireContext(), R.color.fatColor)
        )

        val barData = BarData(dataSet)
        binding.barChart.data = barData

        val xAxis = binding.barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = entries.size
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Protein", "Karbonhidrat", "Yağ"))

        binding.barChart.axisLeft.setDrawGridLines(false)
        binding.barChart.axisRight.setDrawGridLines(false)
        binding.barChart.axisRight.isEnabled = false
        binding.barChart.description.isEnabled = false
        binding.barChart.legend.isEnabled = false
        binding.barChart.invalidate()
        binding.barChart.animateY(1260)
    }

    private fun setDate() {
        val today = LocalDate.now()

        val locale = Locale("tr")

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale)
        val formattedDate = today.format(formatter)

        binding.textViewTodayDate.text = formattedDate
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
