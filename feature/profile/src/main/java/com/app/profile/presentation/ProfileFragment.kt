package com.app.profile.presentation

import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.app.core.data.model.FoodModelParcelize
import com.app.profile.R
import com.app.profile.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var profileAdapter: ProfileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setAvatarImage()

        binding.changePhotoImageView.setOnClickListener {
            showAvatarSelectionDialog()
        }

        binding.btnCalculateNutrition.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_nutritionAnalysisFragment)
        }

        binding.btnUpdateUserInfo.setOnClickListener{
            showUpdateUserInfoDialog()
        }
    }

    private fun setupRecyclerView() {
        profileAdapter = ProfileAdapter { date, foods ->
            Log.d("ProfileFragment", "Selected date: $date, Foods: $foods")

            val action = ProfileFragmentDirections.actionProfileFragmentToFoodHistoryFragment(
                foods.toTypedArray()
            )
            findNavController().navigate(action)
        }
        binding.foodHistoryRecyclerView.apply {
            adapter = profileAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            profileViewModel.foodUiState.collect { foodUiState ->
                val foodParcelizeList = foodUiState.foodList.map { food ->
                    FoodModelParcelize(
                        food.id, food.label, food.calories, food.protein, food.fat,
                        food.carbohydrates, food.image, food.date, food.year,
                        food.yearOfMonth, food.dayOfMonth, food.dayName
                    )
                }
                profileAdapter.updateData(foodParcelizeList)
                binding.profileProgressBar.visibility = if (foodUiState.isLoading) View.VISIBLE else View.GONE
                foodUiState.error?.let {
                }
            }
        }

        lifecycleScope.launch {
            profileViewModel.userUiState.collect { userUiState ->
                userUiState.user?.let { user ->
                    binding.userHeightEditText.setText(user.height?.toString() ?: "")
                    binding.userWeightEditText.setText(user.weight?.toString() ?: "")
                    binding.userAgeEditText.setText(user.age?.toString() ?: "")
                    binding.userEmailEditText.setText(user.email ?: "")
                }

                binding.profileProgressBar.visibility = if (userUiState.isLoading) View.VISIBLE else View.GONE
                userUiState.error?.let {

                }
            }
        }
    }

    private fun showAvatarSelectionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_avatar, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val avatar1ImageView = dialogView.findViewById<ImageView>(R.id.avatar1ImageView)
        val avatar2ImageView = dialogView.findViewById<ImageView>(R.id.avatar2ImageView)
        val avatar3ImageView = dialogView.findViewById<ImageView>(R.id.avatar3ImageView)
        val avatar4ImageView = dialogView.findViewById<ImageView>(R.id.avatar4ImageView)

        avatar1ImageView.setOnClickListener {
            setAvatarAndSave(R.drawable.ic_profile_photo_1)
            dialog.dismiss()
        }

        avatar2ImageView.setOnClickListener {
            setAvatarAndSave(R.drawable.ic_profile_photo_2)
            dialog.dismiss()
        }

        avatar3ImageView.setOnClickListener {
            setAvatarAndSave(R.drawable.ic_profile_photo_3)
            dialog.dismiss()
        }

        avatar4ImageView.setOnClickListener {
            setAvatarAndSave(R.drawable.ic_profile_photo_4)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setAvatarAndSave(resourceId: Int) {
        binding.userPhotoImageView.setImageResource(resourceId)
        saveSelectedAvatar(resourceId)
    }

    private fun saveSelectedAvatar(resourceId: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("SelectedAvatar", resourceId).apply()
    }

    private fun getSelectedAvatar(): Int {
        val sharedPreferences = requireContext().getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("SelectedAvatar", R.drawable.ic_profile_photo_2)
    }

    private fun setAvatarImage() {
        val selectedAvatar = getSelectedAvatar()
        binding.userPhotoImageView.setImageResource(selectedAvatar)
    }


    private fun showUpdateUserInfoDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_user_info, null)
        val heightEditText: EditText = dialogView.findViewById(R.id.heightEditText)
        val weightEditText: EditText = dialogView.findViewById(R.id.weightEditText)
        val ageEditText: EditText = dialogView.findViewById(R.id.ageEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Profil Güncelleme")
            .setView(dialogView)
            .setPositiveButton("Güncelle") { _, _ ->
                val height = heightEditText.text.toString()
                val weight = weightEditText.text.toString()
                val age = ageEditText.text.toString()
                profileViewModel.updateUserInfo(height, weight, age)
            }
            .setNegativeButton("İptal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
