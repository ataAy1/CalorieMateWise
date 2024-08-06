package com.app.home.presentation



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.data.model.FoodModel
import com.app.home.domain.usecase.GetAllFoodsUseCase
import com.app.home.domain.usecase.GetTodayFoodsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllFoodsUseCase: GetAllFoodsUseCase,
    private val getTodayFoodsUseCase: GetTodayFoodsUseCase,
) : ViewModel() {

    private val _todayFoods = MutableStateFlow<List<FoodModel>>(emptyList())
    val todayFoods: StateFlow<List<FoodModel>> get() = _todayFoods

    private val _allFoods = MutableStateFlow<List<FoodModel>>(emptyList())
    val allFoods: StateFlow<List<FoodModel>> get() = _allFoods

    fun getAllFoods() {
        viewModelScope.launch {
            getAllFoodsUseCase()
                .collect { foods ->
                    Log.d("ViewModelDebug", "Collected all foods: $foods")
                    _allFoods.value = foods
                }
        }
    }

    fun getTodayFoods() {
        viewModelScope.launch {
            getTodayFoodsUseCase()
                .collect { foods ->
                    Log.d("ViewModelDebug", "Collected foods by date ($): $foods")
                    _todayFoods.value = foods
                }
        }
    }
}
