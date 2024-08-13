package com.app.detail.presentation

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
import com.app.detail.data.model.FoodModel
import com.app.detail.databinding.FragmentSearchDetailBinding
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
            Log.d(
                "SearchDetailFragment",
                "ParsedFood received: ${foodDetail.label}, Calories: ${foodDetail.nutrients.ENERC_KCAL}"
            )

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


                val foodModel = FoodModel(
                    id = null,
                    label = foodDetail.label,
                    calories = foodDetail.nutrients.ENERC_KCAL.toInt(),
                    protein = foodDetail.nutrients.PROCNT.toInt(),
                    fat = foodDetail.nutrients.FAT.toInt(),
                    carbohydrates = foodDetail.nutrients.CHOCDF.toInt(),
                    image = foodDetail.image,
                    date = today.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", locale)),
                    year = year,
                    dayName = dayName,
                    dayOfMonth = day.toString(),
                    yearOfMonth = yearMonth
                )

                viewModel.addFoodToMeal(foodModel)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
