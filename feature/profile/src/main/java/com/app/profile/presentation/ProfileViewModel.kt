package com.app.profile.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.domain.model.NutritionResult
import com.app.profile.domain.usecase.CalculateNutritionUseCase
import com.app.profile.domain.usecase.FetchFoodListUseCase
import com.app.profile.domain.usecase.FetchUserInfoUseCase
import com.app.profile.domain.usecase.LogOutUseCase
import com.app.profile.domain.usecase.UpdateUserInfoUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fetchFoodListUseCase: FetchFoodListUseCase,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val calculateNutritionUseCase: CalculateNutritionUseCase,
    private val logOutUseCase: LogOutUseCase
    ) : ViewModel() {

    private val _foodUiState = MutableStateFlow(ProfileUIState())
    val foodUiState: StateFlow<ProfileUIState> = _foodUiState

    private val _userUiState = MutableStateFlow(UserUIState())
    val userUiState: StateFlow<UserUIState> = _userUiState

    private val _nutritionResult = MutableStateFlow<NutritionResult?>(null)
    val nutritionResult: StateFlow<NutritionResult?> = _nutritionResult

    private val _nutritionSaveState = MutableStateFlow<NutritionUIState?>(null)
    val nutritionSaveState: StateFlow<NutritionUIState?> = _nutritionSaveState

    private val _logoutUiState = MutableStateFlow<LogoutUiState>(LogoutUiState.Idle)
    val logoutUiState: StateFlow<LogoutUiState> = _logoutUiState

    init {
        getAllFoods()
        getUserInfo()
    }

    fun getAllFoods() {
        viewModelScope.launch {
            _foodUiState.value = _foodUiState.value.copy(isLoading = true)
            fetchFoodListUseCase.execute()
                .collect { foods ->
                    Log.d("ViewModelDebug", "Collected foods by date: $foods")
                    _foodUiState.value = _foodUiState.value.copy(foodList = foods, isLoading = false)
                }
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            _userUiState.value = _userUiState.value.copy(isLoading = true)
            fetchUserInfoUseCase.execute()
                .collect { user ->
                    Log.d("ViewModelDebug", "Fetched user info: $user")
                    _userUiState.value = _userUiState.value.copy(user = user, isLoading = false)
                }
        }
    }


    fun updateUserInfo(height: String, weight: String, age: String) {
        viewModelScope.launch {
            updateUserInfoUseCase.execute(height, weight, age)
            getUserInfo()
        }
    }

    fun calculateNutrition(age: Int, height: Int, weight: Int, activityLevel: String, gender: String): NutritionResult {
        val result = calculateNutritionUseCase.calculate(age, height, weight, activityLevel, gender)
        _nutritionResult.value = result
        return result
    }


    fun saveNutritionAnalysis(result: NutritionResult) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            viewModelScope.launch {
                _nutritionSaveState.value = NutritionUIState(isLoading = true)
                try {
                    calculateNutritionUseCase.saveNutritionAnalysis(userId, result)
                    _nutritionSaveState.value = NutritionUIState(saveSuccess = true)
                    Log.d("ViewModel", "Nutrition analysis saved successfully")
                } catch (e: Exception) {
                    _nutritionSaveState.value = NutritionUIState(error = e.message)
                    Log.e("ViewModelError", "Error saving nutrition analysis", e)
                }
            }
        } else {
            _nutritionSaveState.value = NutritionUIState(error = "User not authenticated")
        }
    }

    fun logOut() {
        viewModelScope.launch {
            try {
                _logoutUiState.value = LogoutUiState.Loading
                logOutUseCase.execute()
                _logoutUiState.value = LogoutUiState.Success
            } catch (e: Exception) {
                _logoutUiState.value = LogoutUiState.Error(e.message ?: "Logout failed")
            }
        }
    }
}