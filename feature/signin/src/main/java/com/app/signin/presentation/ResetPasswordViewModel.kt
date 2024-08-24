package com.app.signin.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.signin.domain.usecase.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val resetPasswordUseCase: ResetPasswordUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUIState())
    val uiState: StateFlow<ResetPasswordUIState> = _uiState

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = ResetPasswordUIState(isLoading = true)
            resetPasswordUseCase.resetPassword(email).collect { result ->
                _uiState.value = if (result.isSuccess) {
                    ResetPasswordUIState(success = true)
                } else {
                    ResetPasswordUIState(error = result.exceptionOrNull()?.message)
                }
            }
        }
    }
}
