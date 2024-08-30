package com.app.profile.presentation

sealed class LogoutUiState {
    object Idle : LogoutUiState()
    object Loading : LogoutUiState()
    object Success : LogoutUiState()
    data class Error(val message: String) : LogoutUiState()
}