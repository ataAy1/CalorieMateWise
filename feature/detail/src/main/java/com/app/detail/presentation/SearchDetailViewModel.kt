package com.app.detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.detail.domain.usecase.SearchDetailUseCase
import com.app.detail.data.model.FoodModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchDetailViewModel @Inject constructor(
    private val searchDetailUseCase: SearchDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchDetailUIState())
    val uiState: StateFlow<SearchDetailUIState> = _uiState

    fun addFoodToMeal(food: FoodModel) {
        _uiState.value = SearchDetailUIState(isLoading = true)
        viewModelScope.launch {
            try {
                searchDetailUseCase.addFoodToMeal(food)
                _uiState.value = SearchDetailUIState(isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = SearchDetailUIState(error = e.message ?: "An error occurred")
            }
        }
    }
}
