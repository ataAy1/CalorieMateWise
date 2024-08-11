package com.app.profile.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.profile.domain.usecase.FetchFoodListUseCase
import com.app.profile.domain.usecase.FetchUserInfoUseCase
import com.app.profile.domain.usecase.UpdateUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fetchFoodListUseCase: FetchFoodListUseCase,
    private val fetchUserInfoUseCase: FetchUserInfoUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase

) : ViewModel() {

    private val _foodUiState = MutableStateFlow(ProfileUIState())
    val foodUiState: StateFlow<ProfileUIState> = _foodUiState

    private val _userUiState = MutableStateFlow(UserUIState())
    val userUiState: StateFlow<UserUIState> = _userUiState

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
}