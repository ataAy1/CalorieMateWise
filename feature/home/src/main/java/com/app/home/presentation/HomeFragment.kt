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
                        updateBarChartAndTotalCalorie(emptyList())
                    } else {
                        todayFoodsAdapter.updateData(state.todayFoods)
                        updateBarChartAndTotalCalorie(state.todayFoods)
                    }
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
                    if (state.nutritionResult == null) {
                        binding.textViewWarning.text = "Makro analizinizi henüz ayarlanmamış. Profilim kısmında yapınız!"
                    } else {
                        addRedLimitLines(state.nutritionResult)
                    }
                    state.error?.let { error ->
                        Log.e("HomeFragment", "Error fetching analysis data: $error")
                    }
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
        carbsLimitLine.lineColor = ContextCompat.getColor(requireContext(), R.color.carbohydratesColor)
        carbsLimitLine.textColor = ContextCompat.getColor(requireContext(), R.color.carbohydratesColor)
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


    private fun updateBarChartAndTotalCalorie(foods: List<FoodModel>) {
        val protein = foods.sumOf { it.protein }
        val carbohydrates = foods.sumOf { it.carbohydrates }
        val fat = foods.sumOf { it.fat }
        val totalCaloriesToday = foods.sumOf { it.calories }

        val calorieText = "Bugün tükettiğiniz kalori: <b>$totalCaloriesToday</b>"
        binding.textViewtotalCalories.text = Html.fromHtml(calorieText)

        binding.textViewProtein.text = "Protein: ${protein.toString()}"
        binding.textViewCarbohydrates.text = "Karbonhidrat: ${carbohydrates.toString()}"
        binding.textViewFat.text = "Yağ: ${fat.toString()}"

        val nutritionResult = viewModel.analysisDataState.value.nutritionResult

        nutritionResult?.let {
            val targetCalories = it.calories.toFloat()
            binding.progressBarTargetCalorie.max = targetCalories.toInt()
            binding.progressBarTargetCalorie.progress = totalCaloriesToday.toInt()

            val remainingCalories = targetCalories - totalCaloriesToday

            val message = if (totalCaloriesToday >= targetCalories) {
                "Tebrikler! Hedef kaloriye ulaştınız."
            } else {
                "Hedefinize ulaşmanız için $remainingCalories kalori kaldı."
            }
            binding.textViewTodayDate.text = message
        } ?: run {
            binding.textViewWarning.text = "Makro analizinizi henüz ayarlanmamış. Profilim kısmında yapınız!"
            binding.progressBarTargetCalorie.visibility = View.GONE
        }

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

        val yAxisLeft = binding.barChart.axisLeft
        yAxisLeft.setDrawGridLines(true)
        yAxisLeft.granularity = 40f
        yAxisLeft.axisMinimum = 0f
        yAxisLeft.axisMaximum = 300f

        val yAxisRight = binding.barChart.axisRight
        yAxisRight.setDrawGridLines(false)
        yAxisRight.isEnabled = false

        binding.barChart.description.isEnabled = false
        binding.barChart.legend.isEnabled = false
        binding.barChart.invalidate()
        binding.barChart.animateY(1500)
    }


    private fun setDate() {
        val today = LocalDate.now()

        val locale = Locale("tr")

        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", locale)
        val formattedDate = today.format(formatter)

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
