package com.app.detail.presentation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import coil.load
import com.app.data.dto.ParsedFood
import com.app.detail.data.model.FoodModel
import com.app.detail.databinding.FragmentSearchDetailBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@AndroidEntryPoint
class SearchDetailFragment : Fragment() {

    private var _binding: FragmentSearchDetailBinding? = null
    private val binding get() = _binding!!
    private val args: SearchDetailFragmentArgs by navArgs()
    private val viewModel: SearchDetailViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val foodDetail = args.parsedFood
        if (foodDetail != null) {
            setupPieChart (foodDetail)

            binding.textFoodLabel.text = foodDetail.label.toString()
            binding.textFoodCalories.text = "${foodDetail.nutrients.ENERC_KCAL} kcal".toString()
            binding.imageViewFood.load(foodDetail.image)
            binding.textFoodProtein.text = "${foodDetail.nutrients.PROCNT} g".toString()
            binding.textFoodFat.text = "${foodDetail.nutrients.FAT} g".toString()
            binding.textFoodCarbohydrates.text = "${foodDetail.nutrients.CHOCDF} g".toString()
        } else {
            Log.d("SearchDetailFragment", "No ParsedFood received")
        }

        binding.buttonAddToMeal.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                Log.d("SearchDetailFragment", "Current User: ${user.uid}, Email: ${user.email}")

                val today = LocalDate.now()
                val locale = Locale("tr")

                val year = today.format(DateTimeFormatter.ofPattern("yyyy", locale))
                val yearMonth = today.format(DateTimeFormatter.ofPattern("MM", locale))
                val day = today.dayOfMonth
                val dayName = today.format(DateTimeFormatter.ofPattern("EEEE", locale))
                val weightOfFoodText = binding.weightOfFoodEditText.text.toString()

                val weightOfFood = weightOfFoodText.toIntOrNull()

                if (weightOfFood != null) {
                    val ratio = weightOfFood / 100.0

                    val adjustedCalories = (foodDetail.nutrients.ENERC_KCAL * ratio).toInt()
                    val adjustedProtein = (foodDetail.nutrients.PROCNT * ratio).toInt()
                    val adjustedFat = (foodDetail.nutrients.FAT * ratio).toInt()
                    val adjustedCarbohydrates = (foodDetail.nutrients.CHOCDF * ratio).toInt()

                    val foodModel = FoodModel(
                        id = null,
                        label = foodDetail.label,
                        calories = adjustedCalories,
                        protein = adjustedProtein,
                        weightofFood = weightOfFood,
                        fat = adjustedFat,
                        carbohydrates = adjustedCarbohydrates,
                        image = foodDetail.image,
                        date = today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", locale)),
                        year = year,
                        dayName = dayName,
                        dayOfMonth = day.toString(),
                        yearOfMonth = yearMonth
                    )

                    viewModel.addFoodToMeal(foodModel)

                } else {
                    Toast.makeText(requireContext(), "Lütfen gram olarak, (100,200) şeklinde,geçerli bir sayı girini..", Toast.LENGTH_SHORT).show()
                }

            } else {
                Log.d("SearchDetailFragment", "No user is authenticated")
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.uiState.collect { state ->
                if (state.isLoading) {
                    binding.progressBarAddToMeal.visibility = View.VISIBLE
                } else {
                    binding.progressBarAddToMeal.visibility = View.GONE
                    if (state.isSuccess) {
                        Toast.makeText(context, "Yemek Eklendi", Toast.LENGTH_SHORT).show()
                    } else if (state.error != null) {
                        Toast.makeText(context, "Error: ${state.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun setupPieChart(food: ParsedFood) {
        val pieChart = binding.foodsPieChart

        val entries = listOf(
            PieEntry(food.nutrients.PROCNT.toFloat(), "Protein"),
            PieEntry(food.nutrients.FAT.toFloat(), "Yağ"),
            PieEntry(food.nutrients.CHOCDF.toFloat(), "KarbonHidrat")
        )

        val dataSet = PieDataSet(entries, "Makro Değerleri").apply {
            colors = ColorTemplate.COLORFUL_COLORS.toList()
            valueTextSize = 16f
            valueTextColor = android.graphics.Color.BLACK
        }

        val data = PieData(dataSet)

        pieChart.apply {
            this.data = data
            setDrawEntryLabels(true)
            setDrawHoleEnabled(true)
            holeRadius = 60f
            animateY(1250)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}