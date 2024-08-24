package com.app.home.presentation


import HomeUIState
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.data.model.FoodModel
import com.app.home.domain.usecase.DeleteFoodUseCase
import com.app.home.domain.usecase.GetAllFoodsUseCase
import com.app.home.domain.usecase.GetTodayFoodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodayFoodsUseCase: GetTodayFoodsUseCase,
    private val deleteFoodUseCase: DeleteFoodUseCase,

    ) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> get() = _uiState

    fun getTodayFoods() {
        viewModelScope.launch {
            _uiState.value = HomeUIState(isLoading = true)
            try {
                getTodayFoodsUseCase()
                    .collect { foods ->
                        Log.d("HomeViewModel", "Fetched today's foods: $foods")
                        _uiState.value = HomeUIState(todayFoods = foods, isLoading = false)
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error fetching today's foods", e)
                _uiState.value = HomeUIState(isLoading = false, error = e.message)
            }
        }
    }


    fun deleteFood(foodId: String) {
        viewModelScope.launch {
            try {
                deleteFoodUseCase(foodId)
                getTodayFoods()
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting food item", e)
            }
        }
    }

}