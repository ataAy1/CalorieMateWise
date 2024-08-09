package com.app.profile.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.profile.domain.usecase.FetchFoodListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val fetchFoodListUseCase: FetchFoodListUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState

    init {
        getAllFoods()
    }

    fun getAllFoods() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            fetchFoodListUseCase.execute()
                .collect { foods ->
                    Log.d("ViewModelDebug", "Collected foods by date: $foods")
                    _uiState.value = _uiState.value.copy(foodList = foods, isLoading = false)
                }
        }
    }
}
