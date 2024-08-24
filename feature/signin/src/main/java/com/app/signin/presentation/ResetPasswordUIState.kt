package com.app.signin.presentation

data class ResetPasswordUIState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
