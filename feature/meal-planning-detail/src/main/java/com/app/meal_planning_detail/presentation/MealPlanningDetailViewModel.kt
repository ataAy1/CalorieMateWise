package com.app.meal_planning_detail.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.domain.model.MealPlanUpload
import com.app.meal_planning_detail.domain.usecase.FetchMealsDetailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealPlanningDetailViewModel @Inject constructor(
    private val fetchMealsDetailUseCase: FetchMealsDetailUseCase
) : ViewModel() {

    private val _mealPlanDetailState = MutableStateFlow(MealPlanDetailUIState())
    val mealPlanDetailState: StateFlow<MealPlanDetailUIState> = _mealPlanDetailState

    fun loadMealPlans(date: String, mealSetID: String) {
        viewModelScope.launch {
            _mealPlanDetailState.value = MealPlanDetailUIState(isLoading = true)
            try {
                val result = fetchMealsDetailUseCase.fetchMealPlans(date, mealSetID)
                _mealPlanDetailState.value = MealPlanDetailUIState(mealPlanDetail = result ?: emptyList())
            } catch (e: Exception) {
                _mealPlanDetailState.value = MealPlanDetailUIState(error = e.message)
            }
        }
    }
}
