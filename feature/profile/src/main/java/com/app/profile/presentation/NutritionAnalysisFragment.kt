package com.app.profile.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.app.domain.model.NutritionResult
import com.app.profile.R
import com.app.profile.databinding.FragmentNutritionAnalysisBinding
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NutritionAnalysisFragment : Fragment(R.layout.fragment_nutrition_analysis) {

    private var _binding: FragmentNutritionAnalysisBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentNutritionAnalysisBinding.bind(view)


        binding.backButton.setOnClickListener{
            findNavController().navigateUp()
        }

        binding.btnCalculate.setOnClickListener {
            val age = binding.etAge.text.toString().toIntOrNull()
            val height = binding.etHeight.text.toString().toIntOrNull()
            val weight = binding.etWeight.text.toString().toIntOrNull()
            val activityLevel = binding.spinnerActivityLevel.selectedItem.toString()
            val gender = when (binding.rgGender.checkedRadioButtonId) {
                R.id.rbMale -> "male"
                R.id.rbFemale -> "female"
                else -> null
            }

            if (age != null && height != null && weight != null && gender != null) {
                val result = viewModel.calculateNutrition(age, height, weight, activityLevel, gender)
                displayNutritionAnalysis(result)
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnSave.setOnClickListener {
            val result = viewModel.nutritionResult.value
            if (result != null) {
                viewModel.saveNutritionAnalysis(result)
            } else {
                Toast.makeText(requireContext(), "Lütfen tüm alanları doldurun ve hesaplama yapınız", Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nutritionSaveState.collect { state ->
                if (state != null) {
                    if (state.isLoading) {
                    } else {
                        state.saveSuccess?.let { success ->
                            if (success) {
                                Toast.makeText(requireContext(), "Başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        state.error?.let { error ->
                            Toast.makeText(requireContext(), "Hata: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun displayNutritionAnalysis(result: NutritionResult) {
        val entries = listOf(
            PieEntry(result.protein.toFloat(), "Protein"),
            PieEntry(result.fat.toFloat(), "Yağ"),
            PieEntry(result.carbs.toFloat(), "Karbonhidrat")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        dataSet.valueTextSize = 12f

        val data = PieData(dataSet)

        binding.nutritionAnalysisChart.apply {
            this.data = data
            description.isEnabled = false
            legend.textSize = 10f
            setEntryLabelTextSize(10f)
            setEntryLabelColor(Color.BLACK)
            setHoleColor(Color.WHITE)
            setHoleRadius(44f)
            setTransparentCircleAlpha(0)
            setTransparentCircleRadius(50f)
            animateY(1000)
        }

        binding.nutritionAnalysisChart.invalidate()

        binding.dailyCalorieTextView.text = "Günlük Kalori İhtiyacınız: ${result.calories}"
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
