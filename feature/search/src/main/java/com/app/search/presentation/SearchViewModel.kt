package com.app.search.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.search.data.mapper.SearchMapper
import com.app.search.domain.usecase.SearchUseCase
import com.app.utils.TranslationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUseCase: SearchUseCase,
    private val mapper: SearchMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUIState())
    val uiState: StateFlow<SearchUIState> = _uiState

    fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = SearchUIState(isLoading = true)
            Log.d("SearchUIState", "$query")

            try {
                val translatedQuery = TranslationUtil.translateToEnglish(query)
                searchUseCase.execute(translatedQuery).collect { response ->
                    val parsedFoods = response.parsed.map { parsed ->
                        mapper.mapFoodToParsedFood(parsed.food)
                    }

                    val hintFoods = response.hints.map { hint ->
                        mapper.mapFoodToParsedFood(hint.food)
                    }

                    val combinedResponse = parsedFoods + hintFoods

                    _uiState.value = SearchUIState(
                        combinedResponseState = combinedResponse,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SearchUIState(
                    error = "Exception occurred: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
}
