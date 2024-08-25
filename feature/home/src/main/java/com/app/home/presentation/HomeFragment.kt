package com.app.home.presentation

import TodayFoodsAdapter
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.core.data.model.FoodModel
import com.app.domain.model.NutritionResult
import com.app.home.R
import com.app.home.databinding.FragmentHomeBinding
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.lang.String
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.Exception
import kotlin.Int
import kotlin.getValue
import kotlin.let
import kotlin.run

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

        viewModel.fetchAnalysisData()
        viewModel.getTodayFoods()
        setDate()

        binding.recyclerViewTodayFoods.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewTodayFoods.adapter = todayFoodsAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    binding.progressBarHome.visibility = View.VISIBLE
                } else {
                    binding.progressBarHome.visibility = View.GONE

                    if (state.todayFoods.isNullOrEmpty()) {
                        binding.textViewTodayFood.text = "Bugün bir şey yemediniz."
                        todayFoodsAdapter.updateData(emptyList())
                    } else {
                        todayFoodsAdapter.updateData(state.todayFoods)
                    }

                    updateUI(state.todayFoods, viewModel.analysisDataState.value.nutritionResult)
                }

                state.error?.let { error ->
                    Log.e("HomeFragment", "Error fetching today's foods: $error")
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.analysisDataState.collect { state ->
                if (state.isLoading) {
                    Log.d("HomeFragment", "Loading analysis data...")
                } else {
                    state.error?.let { error ->
                        Log.e("HomeFragment", "Error fetching analysis data: $error")
                    }

                    updateUI(viewModel.uiState.value.todayFoods, state.nutritionResult)
                }
            }
        }
    }

    val todayFoodsAdapter = TodayFoodsAdapter(emptyList()) { food ->
        food.id?.let { id ->
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    viewModel.deleteFood(id)
                } catch (e: Exception) {
                    Log.e("HomeFragment", "Error deleting food: ${e.message}")
                }
            }
        } ?: run {
            Log.e("HomeFragment", "Food id is null")
        }

    }

    private fun updateUI(foods: List<FoodModel>?, nutritionResult: NutritionResult?) {
        if (foods.isNullOrEmpty()) {
            binding.textViewTodayFood.text = "Bugün bir şey yemediniz."
            binding.textViewtotalCalories.text = "0"
            binding.textViewProtein.text = "Protein: 0"
            binding.textViewCarbohydrates.text = "Karbonhidrat: 0"
            binding.textViewFat.text = "Yağ: 0"
            binding.barChart.clear()
            return
        }

        val protein = foods.sumOf { it.protein }
        val carbohydrates = foods.sumOf { it.carbohydrates }
        val fat = foods.sumOf { it.fat }
        val totalCaloriesToday = foods.sumOf { it.calories }

        binding.textViewtotalCalories.text = totalCaloriesToday.toString()
        binding.textViewProtein.text = "Protein: ${protein.toString()}"
        binding.textViewCarbohydrates.text = "Karbonhidrat: ${carbohydrates.toString()}"
        binding.textViewFat.text = "Yağ: ${fat.toString()}"

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

        binding.barChart.axisRight.isEnabled = false
        binding.barChart.description.isEnabled = false
        binding.barChart.legend.isEnabled = false
        binding.barChart.invalidate()

        nutritionResult?.let {
            binding.progressBarTargetCalorie.max = it.calories.toInt()
            binding.progressBarTargetCalorie.progress = totalCaloriesToday.toInt()
            binding.textViewMaxCalories.text = "Hedeflenen kalori : ${it.calories}"

            val remainingCalories = it.calories - totalCaloriesToday
            binding.textViewTodayDate.text = if (totalCaloriesToday >= it.calories) {
                "Tebrikler! Hedef kaloriye ulaştınız."
            } else {
                "Hedefinize ulaşmanız için $remainingCalories kalori kaldı."
            }

            addRedLimitLines(it)
            binding.progressBarTargetCalorie.visibility = View.VISIBLE
        } ?: run {
            binding.textViewWarning.text = "Makro analizinizi henüz ayarlanmamış. Profilim Kısmından ayarlarını yapınız"
            binding.progressBarTargetCalorie.visibility = View.GONE
        }
    }


    private fun addRedLimitLines(nutritionResult: NutritionResult) {
        val yAxisLeft = binding.barChart.axisLeft
        yAxisLeft.removeAllLimitLines()

        yAxisLeft.isEnabled = true
        yAxisLeft.setDrawLimitLinesBehindData(true)

        val proteinLimitLine = LimitLine(nutritionResult.protein.toFloat())
        val carbsLimitLine = LimitLine(nutritionResult.carbs.toFloat())
        val fatLimitLine = LimitLine(nutritionResult.fat.toFloat())

        proteinLimitLine.lineWidth = 2f
        proteinLimitLine.lineColor = ContextCompat.getColor(requireContext(), R.color.proteinColor)
        proteinLimitLine.textColor = ContextCompat.getColor(requireContext(), R.color.proteinColor)
        proteinLimitLine.textSize = 12f

        carbsLimitLine.lineWidth = 2f
        carbsLimitLine.lineColor =
            ContextCompat.getColor(requireContext(), R.color.carbohydratesColor)
        carbsLimitLine.textColor =
            ContextCompat.getColor(requireContext(), R.color.carbohydratesColor)
        carbsLimitLine.textSize = 12f

        fatLimitLine.lineWidth = 2f
        fatLimitLine.lineColor = ContextCompat.getColor(requireContext(), R.color.fatColor)
        fatLimitLine.textColor = ContextCompat.getColor(requireContext(), R.color.fatColor)
        fatLimitLine.textSize = 12f

        yAxisLeft.addLimitLine(proteinLimitLine)
        yAxisLeft.addLimitLine(carbsLimitLine)
        yAxisLeft.addLimitLine(fatLimitLine)

        binding.barChart.invalidate()
    }



    private fun setDate() {
        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("tr"))
        val today = LocalDate.now()
        binding.textViewTodayDate.text = today.format(formatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
