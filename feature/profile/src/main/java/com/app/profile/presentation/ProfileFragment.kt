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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.app.core.data.model.FoodModelParcelize
import com.app.profile.R
import com.app.profile.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

        binding.buttonExit.setOnClickListener {
            showLogoutConfirmationDialog()
        }


        binding.changePhotoImageView.setOnClickListener {
            showAvatarSelectionDialog()
        }

        binding.imageViewCalculateNutrition.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_nutritionAnalysisFragment)
        }

        binding.imageViewUpdateUserInfo.setOnClickListener {
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
                if (isAdded) {
                    val foodParcelizeList = foodUiState.foodList.map { food ->
                        FoodModelParcelize(
                            food.id,
                            food.label,
                            food.calories,
                            food.protein,
                            food.weightofFood,
                            food.fat,
                            food.carbohydrates,
                            food.image,
                            food.date,
                            food.year,
                            food.yearOfMonth,
                            food.dayOfMonth,
                            food.dayName
                        )
                    }
                    profileAdapter.updateData(foodParcelizeList)
                    binding.foodProgressBar.visibility =
                        if (foodUiState.isLoading) View.VISIBLE else View.GONE
                    foodUiState.error?.let {
                    }
                }
            }
        }

        lifecycleScope.launch {
            profileViewModel.userUiState.collect { userUiState ->
                if (isAdded) {
                    userUiState.user?.let { user ->
                        binding.userHeightEditText.setText(user.height?.let { "$it cm" } ?: "")
                        binding.userWeightEditText.setText(user.weight?.let { "$it kg" } ?: "")
                        binding.userAgeEditText.setText(user.age?.let { "$it yaş" } ?: "")

                        binding.userEmailEditText.setText(user.email ?: "")
                    }

                    binding.profileProgressBar.visibility =
                        if (userUiState.isLoading) View.VISIBLE else View.GONE
                    userUiState.error?.let {
                    }
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
        val sharedPreferences =
            requireContext().getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("SelectedAvatar", resourceId).apply()
    }

    private fun getSelectedAvatar(): Int {
        val sharedPreferences =
            requireContext().getSharedPreferences("ProfilePreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("SelectedAvatar", R.drawable.ic_profile_photo_2)
    }

    private fun setAvatarImage() {
        val selectedAvatar = getSelectedAvatar()
        binding.userPhotoImageView.setImageResource(selectedAvatar)
    }

    private fun showUpdateUserInfoDialog() {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.dialog_update_user_info, null)
        val heightEditText: EditText = dialogView.findViewById(R.id.heightEditText)
        val weightEditText: EditText = dialogView.findViewById(R.id.weightEditText)
        val ageEditText: EditText = dialogView.findViewById(R.id.ageEditText)

        AlertDialog.Builder(requireContext())
            .setTitle("Profil Güncelleme")
            .setView(dialogView)
            .setPositiveButton("Güncelle") { _, _ ->
                val heightStr = heightEditText.text.toString()
                val weightStr = weightEditText.text.toString()
                val ageStr = ageEditText.text.toString()

                if (heightStr.isBlank() || weightStr.isBlank() || ageStr.isBlank()) {
                    Toast.makeText(context, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT)
                        .show()
                    return@setPositiveButton
                }

                val height = heightStr.toDoubleOrNull()
                val weight = weightStr.toDoubleOrNull()
                val age = ageStr.toIntOrNull()

                if (height == null || height <= 0 || weight == null || weight <= 0 || age == null || age <= 0) {
                    Toast.makeText(
                        context,
                        "Geçersiz veri. Lütfen sayıları doğru girin.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                profileViewModel.updateUserInfo(heightStr, weightStr, ageStr)

                Toast.makeText(context, "Profil başarıyla güncellendi.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("İptal", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeLogoutState() {
        profileViewModel.logoutUiState.onEach { state ->
            when (state) {
                is LogoutUiState.Loading -> {
                }
                is LogoutUiState.Success -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        Toast.makeText(context, "Çıkış Yapıldı ..", Toast.LENGTH_LONG).show()

                        delay(2000)
                        requireActivity().finishAffinity(
                        )
                    }
                }
                is LogoutUiState.Error -> {
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
                else -> Unit
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun showLogoutConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Çıkış Yapma")
        builder.setMessage("Uygulamadan Çıkış Yapmak İstiyorum?")
        builder.setPositiveButton("Evet") { _, _ ->
            profileViewModel.logOut()
            observeLogoutState()
        }
        builder.setNegativeButton("Hayır", null)
        builder.show()
    }

}

