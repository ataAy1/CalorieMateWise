package com.app.detail.presentation

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import com.app.data.dto.ParsedFood
import com.app.detail.R
import com.app.detail.data.model.FoodModel
import com.app.detail.databinding.FragmentSearchDetailBinding
import com.app.utils.ImageUtils
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File
import java.net.URI
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
            setupPieChart(foodDetail)
            binding.textFoodLabel.text =
                "${foodDetail.label} - ${foodDetail.nutrients.ENERC_KCAL} kalori"
            binding.imageViewFood.load(foodDetail.image)
            binding.textFoodProtein.text = "Protein: ${foodDetail.nutrients.PROCNT} g"
            binding.textFoodFat.text = "Yağ: ${foodDetail.nutrients.FAT} g"
            binding.textFoodCarbohydrates.text = "Karbonhidrat: ${foodDetail.nutrients.CHOCDF} g"
        } else {
            Log.d("SearchDetailFragment", "No ParsedFood received")
        }

        binding.buttonAddToMeal.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                binding.buttonAddToMeal.isEnabled = false
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
                    Log.d("SearchDetailFragmentDes", "Attempting to download image from URL: ${foodDetail.image}")

                    if (foodDetail.image.isNotEmpty()&& foodDetail.image.isNotBlank()) {
                        Log.d("SearchDetailFragmentEnter", "Attempting to download image from URL: ${foodDetail.image}")

                        viewLifecycleOwner.lifecycleScope.launch {
                            val imageFile = ImageUtils.downloadImage(foodDetail.image, requireContext())
                            val imageUri = imageFile?.let { Uri.fromFile(it) }

                            if (imageUri != null) {
                                try {
                                    viewModel.uploadImage(imageUri).collect { imageUrl ->
                                        val foodModel = FoodModel(
                                            id = null,
                                            label = foodDetail.label,
                                            calories = adjustedCalories,
                                            protein = adjustedProtein,
                                            weightofFood = weightOfFood,
                                            fat = adjustedFat,
                                            carbohydrates = adjustedCarbohydrates,
                                            image = imageUrl ?: "",
                                            date = today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", locale)),
                                            year = year,
                                            dayName = dayName,
                                            dayOfMonth = day.toString(),
                                            yearOfMonth = yearMonth,
                                            timestamp = System.currentTimeMillis()
                                        )
                                        viewModel.addFoodToMeal(foodModel)
                                    }
                                } catch (e: Exception) {
                                    Log.e("SearchDetailFragment", "Error uploading image", e)
                                    Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Log.e("SearchDetailFragment", "Image URI is null")
                                Toast.makeText(requireContext(), "Image download failed", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        val foodModel = FoodModel(
                            id = null,
                            label = foodDetail.label,
                            calories = adjustedCalories,
                            protein = adjustedProtein,
                            weightofFood = weightOfFood,
                            fat = adjustedFat,
                            carbohydrates = adjustedCarbohydrates,
                            image = "",
                            date = today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", locale)),
                            year = year,
                            dayName = dayName,
                            dayOfMonth = day.toString(),
                            yearOfMonth = yearMonth
                        )
                        viewModel.addFoodToMeal(foodModel)
                    }
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Lütfen gram olarak, (100,200) şeklinde, geçerli bir sayı giriniz.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Log.d("SearchDetailFragment", "No user is authenticated")
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("dene", "No user is authenticated")
            launch {
                viewModel.uiState.collect { state ->
                    binding.progressBarAddToMeal.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    if (state.isSuccess) {
                        Toast.makeText(context, "Yiyecek Eklendi", Toast.LENGTH_SHORT).show()
                        binding.progressBarAddToMeal.visibility = View.GONE
                        binding.buttonAddToMeal.isEnabled = true

                    } else if (state.error != null) {
                        Toast.makeText(context, "Error: ${state.error}", Toast.LENGTH_SHORT).show()
                        binding.progressBarAddToMeal.visibility = View.GONE
                        binding.buttonAddToMeal.isEnabled = true
                    }
                }
            }

            launch {
                Log.d("dene1", "No user is authenticated")

                viewModel.imageUploadUIState.collect { state ->
                    binding.progressBarAddToMeal.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    if (state.imageUrl != null) {
                    } else if (state.error != null) {
                        Toast.makeText(context, "Image upload error: ${state.error}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupPieChart(food: ParsedFood) {
        val pieChart = binding.foodsPieChart
        pieChart.description.isEnabled = false

        val colorsGraph = listOf(
            ColorTemplate.rgb("#FFA726"),
            ColorTemplate.rgb("#66BB6A"),
            ColorTemplate.rgb("#29B6F6")
        )

        val entries = listOf(
            PieEntry(food.nutrients.PROCNT.toFloat(), "Protein"),
            PieEntry(food.nutrients.FAT.toFloat(), "Yağ"),
            PieEntry(food.nutrients.CHOCDF.toFloat(), "KarbonHidrat")
        ).filter { it.value > 0.0f }



        if (entries.isNotEmpty()) {
            val dataSet = PieDataSet(entries, "Makro Değerleri").apply {
                this.colors = colorsGraph
                valueTextSize = 16f
                valueTextColor = android.graphics.Color.BLACK
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        return if (value >= 1) value.toInt().toString() else ""
                    }
                }
            }

            val data = PieData(dataSet)

            pieChart.apply {
                this.data = data
                setDrawEntryLabels(true)
                setDrawHoleEnabled(true)
                holeRadius = 70f
                animateY(1250)

                legend.apply {
                    textColor = Color.BLACK
                    isEnabled = true
                }
                setEntryLabelColor(Color.BLACK)
                setEntryLabelTextSize(12f)
            }
        } else {
            pieChart.clear()
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}
