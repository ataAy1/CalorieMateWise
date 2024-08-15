package com.app.detail.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.detail.domain.usecase.SearchDetailUseCase
import com.app.detail.data.model.FoodModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchDetailViewModel @Inject constructor(
    private val searchDetailUseCase: SearchDetailUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchDetailUIState())
    val uiState: StateFlow<SearchDetailUIState> = _uiState

    private val _imageUploadUIState = MutableStateFlow(ImageUploadUIState())
    val imageUploadUIState: StateFlow<ImageUploadUIState> = _imageUploadUIState

    fun addFoodToMeal(food: FoodModel) {
        _uiState.value = SearchDetailUIState(isLoading = true)
        viewModelScope.launch {
            try {
                searchDetailUseCase.addFoodToMeal(food)
                _uiState.value = SearchDetailUIState(addedFood = true, isSuccess = true)
            } catch (e: Exception) {
                _uiState.value = SearchDetailUIState(error = e.message ?: "An error occurred")
            }
        }
    }

    fun uploadImage(imageUri: Uri): Flow<String> = flow {
        _imageUploadUIState.value = ImageUploadUIState(isLoading = true)
        try {
            val imageUrl = searchDetailUseCase.uploadImage(imageUri)
            _imageUploadUIState.value = ImageUploadUIState(imageUrl = imageUrl)
            emit(imageUrl)
        } catch (e: Exception) {
            _imageUploadUIState.value = ImageUploadUIState(error = e.message ?: "Image upload failed")
            throw e
        }
    }
}
